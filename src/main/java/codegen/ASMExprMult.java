package codegen;

public class ASMExprMult extends ASMExpr{
    ASMExpr l;
    ASMExpr r;

    ASMExprMult(ASMReg l, ASMConst r){
        this.l = l;
        this.r = r;
    }

    ASMExprMult(ASMConst l, ASMReg r){
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
        return l.toString() + " * " + r.toString();
    }
}
