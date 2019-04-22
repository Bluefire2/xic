package kc875.xic_error;

import kc875.lexer.XiTokenLocation;

public class SemanticError extends XiCompilerError {
    public SemanticError(String message, XiTokenLocation location) {
        super(location.toString() + " error:" + message);
    }

    @Override
    public String getErrorKindName() {
        return "Semantic";
    }
}
