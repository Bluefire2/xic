package xic_error;

public abstract class XiCompilerError extends Error {
    XiCompilerError(String message) {
        super(message);
    }

    public String getErrorKindName() {
        return "XiCompiler";
    }

    /**
     * Outputs an error message on STDOUT in the form
     *  <errorKind> error beginning at <inputFilePath>:<line>:<column> description.
     * @param inputFilePath path of the file where the error was encountered.
     */
    public void stdoutError(String inputFilePath) {
        String message = String.format(
                "%s error beginning at %s:%s", this.getErrorKindName(),
                inputFilePath, this.getMessage()
        );
        System.out.println(message);
    }

}
