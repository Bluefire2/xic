package cli;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import lexer.XiToken;
import lexer.XiLexer;


@CommandLine.Command(name = "xic", version = "Xi compiler 0.0")
public class CLI implements Runnable {
    @Option(names = {"-h", "--help"}, usageHelp = true,
            description = "Print a synopsis of options.")
    private boolean optHelp = false;

    @Option(names = {"-l", "--lex"},
            description = "Generate output from lexical analysis.")
    private boolean optLex = false;

    @Parameters(arity = "1..*", paramLabel = "FILE",
            description = "File(s) to process.")
    private File[] optInputFiles;

    @Option(names = "-D", defaultValue = ".",
            description = "Specify where to place generated diagnostic files.")
    private Path path;

    @Override
    public void run() {
        if (Files.exists(path)) {
            if (optLex) {
                lex();
            }
        } else {
            System.out.println(String.format("Error: directory %s not found", path));
        }
    }

    private void lex() {
        for (File f : optInputFiles) {
            String outputFilePath =
                    path + "/" + FilenameUtils.removeExtension(f.getName())
                            + ".lexed";
            try (FileReader fileReader = new FileReader(f);
                 FileWriter fileWriter = new FileWriter(outputFilePath)) {
                XiLexer lexer = new XiLexer(fileReader);

                for (XiToken next = lexer.yylex();
                     next != null;
                     next = lexer.yylex()) {
                    // Tokenize the next string and write the token
                    fileWriter.write(next.toString() + "\n");
                    if (next.isError()) {
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

    public static void main(String[] args) {
        CommandLine.run(new CLI(), args);
    }
}