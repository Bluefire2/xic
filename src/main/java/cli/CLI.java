package cli;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.nio.file.Path;

@CommandLine.Command (name = "xic", version = "Xi compiler 0.0")
public class CLI implements Runnable {
    @Option(names = "--help", usageHelp = true, description = "Add description here...")
    private boolean help = false;

    @Option(names = {"-l", "--lex"}, description = "Add description here...")
    private boolean lex = false;

    @Parameters(arity = "1..*", paramLabel = "FILE", description = "File(s) to process.")
    private File[] inputFiles;

    @Option(names = "-D", defaultValue = ".", description = "Add description here...")
    Path outputPath;

    @Override
    public void run() {
        System.out.println("Hello World!");
    }

    public static void main(String[] args) {
        CommandLine.run(new CLI(), args);
    }
}