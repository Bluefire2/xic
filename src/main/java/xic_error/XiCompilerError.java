package xic_error;

abstract class XiCompilerError extends Error {
    XiCompilerError(String message) {
        super(message);
    }
}
