package cli;

import ast.ASTNode;
import ast.Printable;
import ast.VisitorTypeCheck;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import java_cup.runtime.Symbol;
import lexer.XiLexer;
import lexer.XiTokenFactory;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import polyglot.util.OptimalCodeWriter;
import symboltable.HashMapSymbolTable;
import symboltable.TypeSymTable;
import xi_parser.IxiParser;
import xi_parser.XiParser;
import xi_parser.sym;
import xic_error.LexicalError;
import xic_error.SemanticError;
import xic_error.SyntaxError;
import xic_error.XiCompilerError;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

@CommandLine.Command (name = "xic", version = "Xi compiler 0.0")
public class CLI implements Runnable {
    @Option (names = {"-h", "--help"}, usageHelp = true,
            description = "Print a synopsis of options.")
    private boolean optHelp = false;

    @Option (names = {"-l", "--lex"},
            description = "Generate output from lexical analysis.")
    private boolean optLex = false;

    @Option (names = {"--parse"},
            description = "Generate output from syntactic analysis.")
    private boolean optParse = false;

    @Option (names = {"--debugparse"},
            description = "Parse in debug mode and print AST to terminal.")
    private boolean optDebug = false;

    @Option (names = {"--typecheck"},
            description = "Generate output from semantic analysis.")
    private boolean optTypeCheck = false;

    @Parameters (arity = "1..*", paramLabel = "FILE",
            description = "File(s) to process.")
    private File[] optInputFiles;

    @Option (names = "-D", defaultValue = ".",
            description = "Specify where to place generated diagnostic files.")
    private Path path;

    @Option (names = "-sourcepath", defaultValue = ".",
            description = "Specify where to find input source files.")
    private Path sourcepath;

    @Option (names = "-libpath", defaultValue = ".",
            description = "Specify where to find library interface files.")
    private Path libpath;


    @Override
    public void run() {
        if (Files.exists(path)) {
            if (Files.exists(sourcepath)) {
                if (optLex) {
                    lex();
                }
                if (optParse) {
                    parse();
                }
                if(optTypeCheck) {
                    typeCheck();
                }
            } else {
                System.out.println(String.format("Error: directory %s not found", sourcepath));
            }
        } else {
            System.out.println(String.format("Error: directory %s not found", path));
        }
    }

    private void lex() {
        for (File f : optInputFiles) {
            String outputFilePath = Paths.get(path.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".lexed")
                    .toString();
            String inputFilePath = Paths.get(sourcepath.toString(),
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

    private void parseFiles(File[] files, boolean isInterface) {
        for (File f : files) {
            String outputFilePath = Paths.get(path.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".parsed")
                    .toString();
            String inputFilePath = Paths.get(sourcepath.toString(),
                    f.getPath()).toString();
            try (FileReader fileReader = new FileReader(inputFilePath);
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {
                XiTokenFactory xtf = new XiTokenFactory();
                XiLexer lexer = new XiLexer(fileReader, xtf);
                java_cup.runtime.lr_parser parser;
                if (isInterface) {
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
                    OptimalCodeWriter cw = new OptimalCodeWriter(fileWriter, 80);
                    root = parser.parse().value;
                    printer = new CodeWriterSExpPrinter(cw);
                }
                ((Printable) root).prettyPrint(printer);
                printer.close();
            } catch (LexicalError | SyntaxError | SemanticError e) {
                stdoutError(e, inputFilePath);
                fileoutError(outputFilePath, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void parse() {
        // TODO: major bug: why care about interface files when parsing?
        //  Fixed the filtering anyway
        File[] interfaceFiles = new File(libpath.toString()).listFiles();
        if (interfaceFiles != null) {
            // filter interface files
            interfaceFiles = Arrays.stream(interfaceFiles)
                    .filter(f -> FilenameUtils.getExtension(f.getPath()).equals("ixi"))
                    .toArray(File[]::new);
            parseFiles(interfaceFiles, true);
        }
        parseFiles(optInputFiles, false);
    }

    private void typeCheck() {
        for (File f : optInputFiles) {
            String outputFilePath = Paths.get(path.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".typed")
                    .toString();
            String inputFilePath = Paths.get(sourcepath.toString(),
                    f.getPath()).toString();
            try (FileReader fileReader = new FileReader(inputFilePath);
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {

                XiTokenFactory xtf = new XiTokenFactory();
                XiLexer lexer = new XiLexer(fileReader, xtf);
                XiParser parser = new XiParser(lexer, xtf);
                ASTNode root = (ASTNode) parser.parse().value;
                root.accept(new VisitorTypeCheck(new HashMapSymbolTable<TypeSymTable>(), libpath.toString()));
                fileWriter.write("Valid Xi Program");
            } catch (LexicalError | SyntaxError | SemanticError e) {
                stdoutError(e, inputFilePath);
                fileoutError(outputFilePath, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes the error message in the file.
     * @param outputFilePath path of the file to write in.
     * @param errMessage error message.
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

    /**
     * Outputs an error message on STDOUT in the form
     *  <errorKind> error beginning at <inputFilePath>:<line>:<column> description.
     * @param e error object.
     * @param inputFilePath path of the file where the error was encountered.
     */
    private void stdoutError(XiCompilerError e, String inputFilePath) {
        String message = String.format(
                "%s error beginning at %s:%s", e.getErrorKindName(),
                inputFilePath, e.getMessage()
        );
        System.out.println(message);
    }

    public static void main(String[] args) {
        CommandLine.run(new CLI(), args);
    }
}