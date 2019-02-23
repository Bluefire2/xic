package xic_error;

import lexer.XiTokenLocation;

public class SyntaxError extends XiCompilerError {
    public SyntaxError(String message, XiTokenLocation location) {
        super(location.toString() + " error:" + message);
    }
}
