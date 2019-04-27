package kc875.cli;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.xic.ir.interpret.IRSimulator;
import edu.cornell.cs.cs4120.xic.ir.visit.*;
import java_cup.runtime.Symbol;
import kc875.asm.ASMInstr;
import kc875.asm.visit.RegAllocationNaiveVisitor;
import kc875.ast.ASTNode;
import kc875.ast.Printable;
import kc875.ast.visit.IRTranslationVisitor;
import kc875.ast.visit.TypeCheckVisitor;
import kc875.lexer.XiLexer;
import kc875.lexer.XiTokenFactory;
import kc875.symboltable.HashMapSymbolTable;
import kc875.xi_parser.IxiParser;
import kc875.xi_parser.XiParser;
import kc875.xi_parser.sym;
import kc875.xic_error.LexicalError;
import kc875.xic_error.SemanticError;
import kc875.xic_error.SyntaxError;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import polyglot.util.OptimalCodeWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@CommandLine.Command(
        name = "xic",
        version = "Xi compiler 0.0",
        sortOptions = false
)
public class CLI implements Runnable {
    @Option(names = {"-h", "--help"}, usageHelp = true,
            description = "Print a synopsis of options.")
    private boolean optHelp = false;

    @Option(names = {"--report-opts"},
            description = "Output the allowed compiler optimizations.")
    private boolean optReportOptimizations = false;

    enum Optims {REG, CSE, CF, MC, COPY, DCE}

    enum OptimPhases {INITIAL, FINAL}

    // Map's value is true if user switches on the optimization/phase
    // Initialize values to false for now
    private Map<Optims, Boolean> activeOptims =
            new EnumMap<>(Optims.class) {{
                EnumSet.allOf(Optims.class).forEach(o -> put(o, false));
            }};
    private Map<OptimPhases, Boolean> activeOptimIRPhases =
            new EnumMap<>(OptimPhases.class) {{
                EnumSet.allOf(OptimPhases.class).forEach(p -> put(p, false));
            }};
    private Map<OptimPhases, Boolean> activeOptimCFGPhases =
            new EnumMap<>(OptimPhases.class) {{
                EnumSet.allOf(OptimPhases.class).forEach(p -> put(p, false));
            }};

    @Option(names = {"--debug"}, hidden = true,
            description = "Optional flag which prints to terminal instead of outputting files.")
    private boolean optDebug = false;

    @Option(names = {"-l", "--lex"},
            description = "Generate output from lexical analysis.")
    private boolean optLex = false;

    @Option(names = {"--parse"},
            description = "Generate output from syntactic analysis.")
    private boolean optParse = false;

    @Option(names = {"--typecheck"},
            description = "Generate output from semantic analysis.")
    private boolean optTypeCheck = false;

    @Option(names = {"--irgen"},
            description = "Generate intermediate code.")
    private boolean optIRGen = false;

    @Option(names = {"--irrun"},
            description = "Generate and interpret intermediate code.")
    private boolean optIRRun = false;

    @Option(names = {"--mir"}, hidden = true,
            description = "Do not lower the IR.")
    private boolean optMIR = false;

    // TODO: implement --optir <phase> in code
    @Option(names = {"--optir"},
            description = "Report the intermediate code at the specified " +
                    "phase of optimization")
    private String[] givenOptimIRPhases;

    @Option(names = {"--optcfg"},
            description = "Report the control-flow graph at the specified " +
                    "phase of optimization.")
    private String[] givenOptimCFGPhases;

    @Option(names = "-sourcepath", defaultValue = ".",
            description = "Specify where to find input source files.")
    private Path sourcePath;

    @Option(names = "-libpath", defaultValue = ".",
            description = "Specify where to find input library/interface files.")
    private Path libPath;

    @Option(names = "-D", defaultValue = ".",
            description = "Specify where to place generated diagnostic files.")
    private Path diagnosticPath;

    @Option(names = "-d", defaultValue = ".",
            description = "Specify where to place generated assembly files.")
    private Path asmPath;

    @Option(names = {"-target"},
            description = "Specify the operating system for which to generate code." +
                    "Supported options: linux.")
    private String OS = null;

    @Option(names = {"--commentASM"}, hidden = true,
            description = "Optional flag which adds a comment with the abstract assembly instruction" +
                    "right above the instructions generated from register allocation"
    )
    private boolean optCommentASM = false;

    @Option(names = {"--asm-no-reg"}, hidden = true,
            description = "Do not do register allocation in the asm.")
    private boolean optASMDisableRegAllocation = false;

    @Option(names = {"-O"},
            description = "Disable optimizations.")
    private boolean optDisableOptimization = false;

    // TODO: -O-no-<opt>

    @Parameters(arity = "0..*", paramLabel = "FILE",
            description = "File(s) to process.")
    private File[] optInputFiles;

    /**
     * Returns -1 if path not found, 0 otherwise.
     */
    private int pathExists(Path path) {
        if (!Files.exists(path)) {
            System.err.println("Error: path " + path + " not found");
            return -1;
        }
        return 0;
    }

    @Override
    public void run() {
        // Check for flags that don't require files first
        if (optReportOptimizations) {
            for (Optims o : Optims.values()) {
                System.out.println(o.toString().toLowerCase());
            }
        } else {
            // files required now; check supplied values are valid
            if (optInputFiles == null) {
                System.err.println(
                        "Error: no files given"
                );
                CommandLine.usage(new CLI(), System.out);
                return;
            }
            if (pathExists(diagnosticPath) != 0) return;
            if (pathExists(sourcePath) != 0) return;

            // Check if supplied optimizations and phases are supported
            if (givenOptimIRPhases != null) {
                for (String o : givenOptimIRPhases) {
                    try {
                        activeOptimIRPhases.put(
                                OptimPhases.valueOf(o.toUpperCase()), true
                        );
                    } catch (IllegalArgumentException e) {
                        // Could not convert o to an enum ==> incorrect phase
                        System.err.println(String.format(
                                "Error: phase %s with --optir not supported", o
                        ));
                        return;
                    }
                }
            }
            if (givenOptimCFGPhases != null) {
                for (String o : givenOptimCFGPhases) {
                    try {
                        activeOptimCFGPhases.put(
                                OptimPhases.valueOf(o.toUpperCase()), true
                        );
                    } catch (IllegalArgumentException e) {
                        // Could not convert o to an enum ==> incorrect phase
                        System.err.println(String.format(
                                "Error: phase %s with --optcfg not supported", o
                        ));
                        return;
                    }
                }
            }
            if (optLex) {
                lex();
            }
            if (optParse) {
                parse();
            }
            if (optTypeCheck) {
                typeCheck();
            }
            if (optIRGen) {
                IRGen();
            }
            if (optIRRun) {
                IRRun();
            }
            if (OS != null && OS.equals("linux")) {
                asmGen();
            }
        }
    }

    private void lex() {
        for (File f : optInputFiles) {
            String outputFilePath = Paths.get(diagnosticPath.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".lexed")
                    .toString();
            String inputFilePath = Paths.get(sourcePath.toString(),
                    f.getPath()).toString();
            try (FileReader fileReader = new FileReader(inputFilePath);
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {
                XiLexer lexer = new XiLexer(fileReader, new XiTokenFactory());

                for (Symbol next = lexer.next_token();
                     next.sym != sym.EOF;
                     next = lexer.next_token()) {
                    // Tokenize the next string and write the token
                    fileWriter.write(next.toString() + "\n");
                    if (next.sym == sym.ERROR) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parse() {
        for (File f : optInputFiles) {
            String outputFilePath = Paths.get(diagnosticPath.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".parsed")
                    .toString();
            String inputFilePath = Paths.get(sourcePath.toString(),
                    f.getPath()).toString();
            try (FileReader fileReader = new FileReader(inputFilePath);
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {
                XiTokenFactory xtf = new XiTokenFactory();
                XiLexer lexer = new XiLexer(fileReader, xtf);
                java_cup.runtime.lr_parser parser;
                if (FilenameUtils.getExtension(inputFilePath).equals("ixi")) {
                    parser = new IxiParser(lexer, xtf);
                } else {
                    parser = new XiParser(lexer, xtf);
                }
                CodeWriterSExpPrinter printer;
                Object root;
                if (optDebug) { //debug mode
                    PrintWriter cw = new PrintWriter(System.out);
                    root = parser.debug_parse().value;
                    printer = new CodeWriterSExpPrinter(cw);
                } else {
                    OptimalCodeWriter cw =
                            new OptimalCodeWriter(fileWriter, 80);
                    root = parser.parse().value;
                    printer = new CodeWriterSExpPrinter(cw);
                }
                ((Printable) root).prettyPrint(printer);
                printer.close();
            } catch (LexicalError | SyntaxError | SemanticError e) {
                e.stdoutError(inputFilePath);
                fileoutError(outputFilePath, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ASTNode buildAST(FileReader fileReader) throws Exception {
        XiTokenFactory xtf = new XiTokenFactory();
        XiLexer lexer = new XiLexer(fileReader, xtf);
        XiParser parser = new XiParser(lexer, xtf);
        ASTNode root = (ASTNode) parser.parse().value;
        root.accept(new TypeCheckVisitor(
                new HashMapSymbolTable<>(), libPath.toString()
        ));
        return root;
    }

    private void typeCheck() {
        for (File f : optInputFiles) {
            String outputFilePath = Paths.get(diagnosticPath.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".typed")
                    .toString();
            String inputFilePath = Paths.get(sourcePath.toString(),
                    f.getPath()).toString();
            try (FileReader fileReader = new FileReader(inputFilePath);
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {

                buildAST(fileReader);
                fileWriter.write("Valid Xi Program");
            } catch (LexicalError | SyntaxError | SemanticError e) {
                e.stdoutError(inputFilePath);
                fileoutError(outputFilePath, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private IRNode buildIR(File f, FileReader fileReader) throws Exception {
        ASTNode root = buildAST(fileReader);
        //IR translation and lowering
        IRNode mir = root.accept(new IRTranslationVisitor(
                activeOptims.get(Optims.CF),
                FilenameUtils.removeExtension(f.getName())
        ));
        LoweringVisitor lv = new LoweringVisitor(new IRNodeFactory_c());
        IRNode checkedIR = optMIR ? mir : lv.visit(mir);
        ConstantFoldVisitor cfv = new ConstantFoldVisitor(new IRNodeFactory_c());
        return activeOptims.get(Optims.CF) ? cfv.visit(checkedIR) : checkedIR;
    }

    private void IRGen() {
        for (File f : optInputFiles) {
            String outputFilePath = Paths.get(diagnosticPath.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".ir")
                    .toString();
            String inputFilePath = Paths.get(sourcePath.toString(),
                    f.getPath()).toString();
            try (FileReader fileReader = new FileReader(inputFilePath);
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {

                IRNode foldedIR = buildIR(f, fileReader);
                //pretty-print IR
                CodeWriterSExpPrinter printer;
                if (optDebug) { //debug mode (print to stdout)
                    PrintWriter cw = new PrintWriter(System.out);
                    printer = new CodeWriterSExpPrinter(cw);
                    // IR canonical checker
                    {
                        CheckCanonicalIRVisitor cv =
                                new CheckCanonicalIRVisitor();
                        System.out.print("Canonical?: ");
                        System.out.println(cv.visit(foldedIR));
                    }

                    // IR constant-folding checker
                    {
                        CheckConstFoldedIRVisitor cv =
                                new CheckConstFoldedIRVisitor();
                        System.out.print("Constant-folded?: ");
                        System.out.println(cv.visit(foldedIR));
                    }
                } else {
                    OptimalCodeWriter cw =
                            new OptimalCodeWriter(fileWriter, 80);
                    printer = new CodeWriterSExpPrinter(cw);
                }
                foldedIR.printSExp(printer);
                printer.close();
            } catch (LexicalError | SyntaxError | SemanticError e) {
                e.stdoutError(inputFilePath);
                fileoutError(outputFilePath, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void IRRun() {
        for (File f : optInputFiles) {
            String outputFilePath = Paths.get(diagnosticPath.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".ir.nml")
                    .toString();
            String inputFilePath = Paths.get(sourcePath.toString(),
                    f.getPath()).toString();
            try (FileReader fileReader = new FileReader(inputFilePath);
                 FileOutputStream fos = new FileOutputStream(outputFilePath)) {

                IRNode foldedIR = buildIR(f, fileReader);
                //Interpreting
                if (!optDebug) {
                    System.setOut(new PrintStream(fos)); // stdout --> file
                }
                IRSimulator sim = new IRSimulator((IRCompUnit) foldedIR);
                sim.call("_Imain_paai", 0);
            } catch (LexicalError | SyntaxError | SemanticError e) {
                e.stdoutError(inputFilePath);
                fileoutError(outputFilePath, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // reset the standard output stream
                System.setOut(new PrintStream(new FileOutputStream(
                        FileDescriptor.out
                )));
            }
        }
    }

    private static final String INDENT_ASM = "   ";

    /**
     * Writes the asm file prologue (intel syntax, global func etc.) to the
     * file writer.
     *
     * @param fileWriter file writer.
     * @throws IOException when problems with fileWriter.
     */
    private void asmFilePrologueWrite(FileWriter fileWriter) throws IOException {
        fileWriter.write(INDENT_ASM + ".intel_syntax noprefix\n");
        fileWriter.write(INDENT_ASM + ".text\n");
        fileWriter.write(INDENT_ASM + ".globl" +
                INDENT_ASM + "_Imain_paai\n");
    }

    private void asmGen() {
        for (File f : optInputFiles) {
            String outputFilePath = Paths.get(asmPath.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".s")
                    .toString();
            String inputFilePath = Paths.get(sourcePath.toString(),
                    f.getPath()).toString();
            try (FileReader fileReader = new FileReader(inputFilePath);
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {

                IRNode foldedIR = buildIR(f, fileReader);
                ASMTranslationVisitor asmVisitor = new ASMTranslationVisitor();
                RegAllocationNaiveVisitor regVisitor =
                        new RegAllocationNaiveVisitor(optCommentASM);
                List<ASMInstr> instrs = asmVisitor.visit((IRCompUnit) foldedIR);
                if (!optASMDisableRegAllocation) {
                    instrs = regVisitor.allocate(instrs);
                }
                asmFilePrologueWrite(fileWriter);
                for (ASMInstr i : instrs) {
                    fileWriter.write(i.toString() + "\n");
                }
            } catch (LexicalError | SyntaxError | SemanticError e) {
                e.stdoutError(inputFilePath);
                fileoutError(outputFilePath, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes the error message in the file.
     *
     * @param outputFilePath path of the file to write in.
     * @param errMessage     error message.
     */
    private void fileoutError(String outputFilePath, String errMessage) {
        try {
            FileWriter fw = new FileWriter(outputFilePath);
            fw.write(errMessage);
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CommandLine.run(new CLI(), args);
    }
}
