package symboltable;

import ast.MetaType;

public class VarCtxType extends CtxType {
    private MetaType type;

    public VarCtxType(MetaType t) {
        this.type = t;
    }

    public MetaType getType() {
        return type;
    }
}
