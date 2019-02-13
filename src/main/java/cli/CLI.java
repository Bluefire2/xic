package cli;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import lexer.LexicalError;
import lexer.XiLexer;
import lexer.XiTokenFactory;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import polyglot.util.OptimalCodeWriter;
import xi_parser.Printable;
import xi_parser.SyntaxError;
import xi_parser.XiParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java_cup.runtime.*;
import xi_parser.sym;

@CommandLine.Command(name = "xic", version = "Xi compiler 0.0")
public class CLI implements Runnable {
    @Option(names = {"-h", "--help"}, usageHelp = true,
            description = "Print a synopsis of options.")
    private boolean optHelp = false;

    @Option(names = {"-l", "--lex"},
            description = "Generate output from lexical analysis.")
    private boolean optLex = false;

    @Option(names = {"--parse"},
            description = "Generate output from syntactic analysis.")
    private boolean optParse = false;

    @Option(names = {"--debugparse"},
            description = "Parse in debug mode and print AST to terminal.")
    private boolean optDebug = false;

    @Parameters(arity = "1..*", paramLabel = "FILE",
            description = "File(s) to process.")
    private File[] optInputFiles;

    @Option(names = "-D", defaultValue = ".",
            description = "Specify where to place generated diagnostic files.")
    private Path path;

    @Option(names = "-sourcepath", defaultValue = ".",
            description = "Specify where to find input source files.")
    private Path sourcepath;

    @Override
    public void run() {
        if (Files.exists(path)) {
            if(Files.exists(sourcepath)) {
                if (optLex) {
                    lex();
                }
                if (optParse) {
                    parse();
                }
            }
            else {
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

    private void parse() {
        for (File f : optInputFiles) {
            String outputFilePath = Paths.get(path.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".parsed")
                    .toString();
            String inputFilePath = Paths.get(sourcepath.toString(),
                    f.getPath()).toString();
            try (FileReader fileReader = new FileReader(inputFilePath);
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {

                XiTokenFactory xtf = new XiTokenFactory();
                XiLexer lexer = new XiLexer(fileReader, xtf);
                XiParser parser = new XiParser(lexer, xtf);
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
            } catch (LexicalError e) {
                System.out.println("Lexical Error");
                System.out.println(e.getMessage());
                fileoutError(outputFilePath, e.getMessage());
            } catch (SyntaxError e) {
                System.out.println("Syntax Error");
                System.out.println(e.getMessage());
                fileoutError(outputFilePath, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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