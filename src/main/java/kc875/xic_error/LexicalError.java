package kc875.xic_error;

import kc875.lexer.XiTokenLocation;

public class LexicalError extends XiCompilerError {
    public LexicalError(String message, XiTokenLocation location) {
        super(location.toString() + " error:" + message);
    }

    @Override
    public String getErrorKindName() {
        return "Lexical";
    }
}
