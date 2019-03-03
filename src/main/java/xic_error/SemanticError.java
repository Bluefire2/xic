package xic_error;

import lexer.XiTokenLocation;

public class SemanticError extends XiCompilerError {
    public SemanticError(String message, XiTokenLocation location) {
        super(location.toString() + " error:" + message);
    }

    @Override
    public String getErrorKindName() {
        return "Semantic";
    }
}
