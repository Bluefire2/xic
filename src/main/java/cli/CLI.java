package cli;

import lexer.XiLexer;
import lexer.XiTokenFactory;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import xi_parser.XiParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
            try (FileReader fileReader = new FileReader(
                    sourcepath.toString() + "/" + f.getPath());
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {
                XiLexer lexer = new XiLexer(fileReader, new XiTokenFactory());

                for (Symbol next = lexer.next_token();
                     next != null && next.sym != sym.EOF;
                     next = lexer.next_token()) {
                    // Tokenize the next string and write the token
                    fileWriter.write(next.toString() + "\n");
                    if (next.sym == sym.ERROR) {
                        // TODO: should we stop the compiler on a lexer error?
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private void parse() {
        for (File f : optInputFiles) {
            String outputFilePath = Paths.get(path.toString(),
                    FilenameUtils.removeExtension(f.getName()) + ".parsed")
                    .toString();
            try (FileReader fileReader = new FileReader(
                    sourcepath.toString() + "/" + f.getPath());
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {

                XiLexer lexer = new XiLexer(fileReader);
                XiParser parser = new XiParser(lexer, new XiTokenFactory());
                parser.parse();

                //TODO: get ast node from parser.parse()

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    public static void main(String[] args) {
        CommandLine.run(new CLI(), args);
    }
}