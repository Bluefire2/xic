package xic_error;

import lexer.XiTokenLocation;

public class SemanticUnresolvedNameError extends SemanticError {
    public SemanticUnresolvedNameError(String name, XiTokenLocation location) {
        super(String.format("Name %s cannot be resolved", name), location);
    }
}
