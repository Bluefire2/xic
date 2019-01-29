package cli;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

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
        System.out.println("Hello World!");
        System.out.println(path);

        XiLexer lexer;

        for (File f : optInputFiles) {
            try (FileReader fileReader = new FileReader(f);
                 FileWriter fileWriter = new FileWriter(path + "/" + f.getName() + ".lexed")) {
                lexer = new XiLexer(fileReader);
                while (true) {
                    fileWriter.write(lexer.yyline + ":" + lexer.yycolumn + " " + lexer.yylex().toString());
                }
            }
            catch(Exception e) {
                continue;
            }
        }
    }

    public static void main(String[] args) {
        CommandLine.run(new CLI(), args);
    }
}