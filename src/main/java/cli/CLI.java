package cli;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import lexer.XiToken;
import lexer.XiLexer;


@CommandLine.Command(name = "xic", version = "Xi compiler 0.0")
public class CLI implements Runnable {
    @Option(names = {"-h", "--help"}, usageHelp = true,
            description = "Print a synopsis of options")
    private boolean optHelp = false;

    @Option(names = {"-l", "--lex"},
            description = "Generate output from lexical analysis")
    private boolean optLex = false;

    @Parameters(arity = "1..*", paramLabel = "FILE",
            description = "File(s) to process.")
    private File[] optInputFiles;

    @Option(names = "-D", defaultValue = ".",
            description = "Specify where to place generated diagnostic files")
    private Path path;

    @Override
    public void run() {
        if (optLex) {
            XiLexer lexer;

            for (File f : optInputFiles) {
                String outputFilePath = path + "/" + FilenameUtils.removeExtension(f.getName()) + ".lexed";
                try (FileReader fileReader = new FileReader(f);
                     FileWriter fileWriter = new FileWriter(outputFilePath)) {
                    lexer = new XiLexer(fileReader);
                    try {
                        XiToken next = lexer.yylex();
                        while(next != null) {
                            fileWriter.write(next.toString() + '\n');
                            next = lexer.yylex();
                        }
                    }
                    catch (Error e) {
                        fileWriter.write(e.getMessage() + '\n');
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } else {

        }
    }

    public static void main(String[] args) {
        CommandLine.run(new CLI(), args);
    }
}