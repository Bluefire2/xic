package xic_error;

public abstract class XiCompilerError extends Error {
    XiCompilerError(String message) {
        super(message);
    }

    public String getErrorKindName() {
        return "XiCompiler";
    }
}
