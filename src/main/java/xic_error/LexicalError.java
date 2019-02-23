package xic_error;

import lexer.XiTokenLocation;

public class LexicalError extends XiCompilerError {
    public LexicalError(String message, XiTokenLocation location) {
        super(location.toString() + " error:" + message);
    }
}
