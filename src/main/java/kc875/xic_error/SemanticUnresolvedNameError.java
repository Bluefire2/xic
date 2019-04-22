package kc875.xic_error;

import kc875.lexer.XiTokenLocation;

public class SemanticUnresolvedNameError extends SemanticError {
    public SemanticUnresolvedNameError(String name, XiTokenLocation location) {
        super(String.format("Name %s cannot be resolved", name), location);
    }
}
