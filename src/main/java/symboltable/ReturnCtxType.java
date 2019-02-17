package symboltable;
import ast.MetaType;

public class ReturnCtxType extends CtxType {
    private MetaType returnType;

    public ReturnCtxType(MetaType returnType) {
        this.returnType = returnType;
    }

    public MetaType getReturnType() {
        return returnType;
    }
}
