package codegen;

public class ASMExprAdd extends ASMExpr {
    ASMExpr l;
    ASMExpr r;

    ASMExprAdd(ASMExpr l, ASMExpr r){
        this.l = l;
        this.r = r;
    }

    public ASMExpr getL() {
        return l;
    }

    public ASMExpr getR() {
        return r;
    }

    @Override
    public String toString(){
        return l.toString() + " + " + r.toString();
    }
}
