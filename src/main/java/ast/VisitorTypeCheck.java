package ast;

import polyglot.util.Pair;
import symboltable.*;

import java.util.List;

public class VisitorTypeCheck implements VisitorAST {
    private SymbolTable symTable;
    private String RETURN_KEY = "__return__";

    public VisitorTypeCheck(SymbolTable symTable){
        this.symTable = symTable;
    }

    public SymbolTable getSymTable() {
        return symTable;
    }

    @Override
    public void visit(ExprBinop node) throws SemanticErrorException {
        TypeT lType = node.getLeftExpr().getTypeCheckType();
        TypeT rType = node.getRightExpr().getTypeCheckType();

        boolean lTypeIsInt = lType instanceof TypeTTauInt;
        boolean lTypeIsBool = lType instanceof TypeTTauBool;
        boolean lTypeIsArray = lType instanceof TypeTTauArray;
        boolean rTypeIsInt = rType instanceof TypeTTauInt;
        boolean rTypeIsBool = rType instanceof TypeTTauBool;
        boolean rTypeIsArray = rType instanceof TypeTTauArray;

        switch (node.getOp()) {
            case PLUS:
                if (lTypeIsInt && rTypeIsInt) {
                    node.setTypeCheckType(new TypeTTauInt());
                    break;
                } else if (lTypeIsArray && rTypeIsArray) {
                    TypeTTauArray larr = (TypeTTauArray) lType;
                    TypeTTauArray rarr = (TypeTTauArray) rType;
                    if (larr.getTypeTTau().equals(rarr.getTypeTTau())) {
                        node.setTypeCheckType(larr);
                        break;
                    }
                }
                throw new SemanticErrorException("Operator + cannot be applied to" +
                        lType.toString() + " and " + rType.toString(), node.left, node.right);
            case MINUS:
            case MULT:
            case HI_MULT:
            case DIV:
            case MOD:
                if (lTypeIsInt && rTypeIsInt) {
                    node.setTypeCheckType(new TypeTTauInt());
                    break;
                }
                throw new SemanticErrorException("Operator" + node.opToString() + "cannot be applied to" +
                        lType.toString() + " and " + rType.toString(), node.left, node.right);
            case EQEQ:
            case NEQ:
                if ((lTypeIsInt && rTypeIsInt) || (lTypeIsBool && rTypeIsBool)) {
                    node.setTypeCheckType(new TypeTTauBool());
                    break;
                }
                if (lTypeIsArray && rTypeIsArray) {
                    TypeTTauArray lTau = (TypeTTauArray) lType;
                    TypeTTauArray rTau = (TypeTTauArray) rType;
                    if (lTau.getTypeTTau().equals(rTau.getTypeTTau())) {
                        node.setTypeCheckType(new TypeTTauBool());
                        break;
                    }
                }
                throw new SemanticErrorException("Operator" + node.opToString() + "cannot be applied to" +
                        lType.toString() + " and " + rType.toString(), node.left, node.right);
            case GT:
            case LT:
            case GTEQ:
            case LTEQ:
                if (lTypeIsInt && rTypeIsInt) {
                    node.setTypeCheckType(new TypeTTauBool());
                    break;
                }
                throw new SemanticErrorException("Operator" + node.opToString() + "cannot be applied to" +
                        lType.toString() + " and " + rType.toString(), node.left, node.right);
            case AND:
            case OR:
                if (lTypeIsBool && rTypeIsBool) {
                    node.setTypeCheckType(new TypeTTauBool());
                    break;
                }
                throw new SemanticErrorException("Operator" + node.opToString() + "cannot be applied to" +
                        lType.toString() + " and " + rType.toString(), node.left, node.right);
            default:
                throw new IllegalArgumentException("Operation Type of " +
                        "Binop node is invalid");
        }
    }

    @Override
    public void visit(ExprBoolLiteral node) {
        node.setTypeCheckType(new TypeTTauBool());
    }

    @Override
    public void visit(ExprFunctionCall node) {
        String name = node.getName();
        try {
            TypeSymTable t = symTable.lookup(name);
            if (t instanceof TypeSymTableFunc) {
                TypeT inTypes = ((TypeSymTableFunc) t).getInput();
                TypeT outTypes = ((TypeSymTableFunc) t).getOutput();
                if (!(outTypes instanceof TypeTUnit)) {
                    // non-unit return type
                    if (inTypes instanceof TypeTUnit) {
                        // procedure with no args
                        node.setTypeCheckType(outTypes);
                    } else if (inTypes instanceof TypeTTau
                            && node.getArgs().size() == 1
                            && node.getArgs().get(0).getTypeCheckType().equals(inTypes)) {
                        // procedure with 1 arg
                        node.setTypeCheckType(outTypes);
                    } else if (inTypes instanceof TypeTList) {
                        // procedure with >= 2 args
                        List<TypeTTau> inTauList = ((TypeTList) inTypes).getTTauList();
                        List<Expr> funcArgs = node.getArgs();
                        if (inTauList.size() == funcArgs.size()) {
                            for (int i = 0; i < funcArgs.size(); ++i) {
                                if (funcArgs.get(i).getTypeCheckType() != inTauList.get(i)) {
                                    // TODO: throw error: corresponding type
                                    //  and tau don't match
                                }
                            }
                            // func args and func sig match
                            node.setTypeCheckType(outTypes);
                        } else {
                            // TODO: throw error: arg and type length not equal
                        }
                    }
                } else {
                    // TODO: throw error: function call expressions must not
                    //  return TUnit
                }
            } else {
                //TODO: throw error
            }
        } catch (NotFoundException e){
            //TODO: handle exception
        }
    }

    @Override
    public void visit(ExprId node) {
        String name = node.getName();
        try {
            TypeSymTable t = symTable.lookup(name);
            if (t instanceof TypeSymTableVar) {
                node.setTypeCheckType(((TypeSymTableVar) t).getTypeTTau());
            } else {
                //TODO: throw error
            }
        } catch (NotFoundException e){
            //TODO: handle exception
        }
    }

    @Override
    public void visit(ExprIndex node) {
        Expr array = node.getArray();
        Expr idx = node.getIndex();
        TypeT at = array.getTypeCheckType();
        TypeT it = idx.getTypeCheckType();
        if (at instanceof TypeTTauArray && it instanceof TypeTTauInt) {
            node.setTypeCheckType(((TypeTTauArray) at).getTypeTTau());
        } else {
            //TODO: throw error
        }
    }

    @Override
    public void visit(ExprIntLiteral node) {
        node.setTypeCheckType(new TypeTTauInt());
    }

    @Override
    public void visit(ExprLength node) {
        if (node.getTypeCheckType() instanceof TypeTTauArray){
            node.setTypeCheckType(new TypeTTauInt());
        } else {
            //TODO: throw error
        }
    }

    @Override
    public void visit(ExprArrayLiteral node) { //TODO: handle lengths somehow
        List<Expr> contents = node.getContents();
        int length = node.getLength();
        if (length == 0) {
            // TODO: this makes the tau type of this array node null,
            //  potentially resulting in unchecked NullPointerExceptions
            node.setTypeCheckType(new TypeTTauArray());
        } else {
            TypeT init = contents.get(0).getTypeCheckType();
            if (init instanceof TypeTTau) {
                TypeTTau initTau = (TypeTTau) init;
                for (Expr e : contents) {
                    if (!initTau.equals(e.getTypeCheckType())) {
                        //TODO: throw error
                    }
                }
                // all taus are equal
                node.setTypeCheckType(new TypeTTauArray(initTau));
            } else {
                // TODO: throw error, init is not tau
            }
        }
    }

    @Override
    public void visit(ExprUnop node) {
        TypeT et = node.getExpr().getTypeCheckType();
        switch (node.getOp()) {
            case NOT:
                if (et instanceof TypeTTauBool) {
                    node.setTypeCheckType(new TypeTTauBool());
                    break;
                }
                //TODO: throw error with position
            case UMINUS:
                if (et instanceof TypeTTauInt) {
                    node.setTypeCheckType(new TypeTTauInt());
                    break;
                }
                //TODO: throw error with position
        }
    }

    @Override
    public void visit(AssignableIndex node) {

    }

    @Override
    public void visit(AssignableUnderscore node) { //Do nothing
    }

    @Override
    public void visit(AssignableId node) throws SemanticErrorException {
        String id = node.getId().getName();
        try {
            symTable.lookup(id);
        }
        catch (NotFoundException e) {
            throw new SemanticErrorException("Uninitialized identifier " + id,
                    node.left, node.right);
        }
    }

    @Override
    public void visit(StmtReturn node) {

    }

    @Override
    public void visit(StmtAssign node) {

    }

    @Override
    public void visit(StmtDecl node) {
        for (TypeDeclVar d : node.getDecls()) {
            TypeSymTableVar dt = new TypeSymTableVar((TypeTTau) d.typeOf());
            for (String did : d.varsOf()) {
                symTable.add(did, dt);
            }
        }
    }

    @Override
    public void visit(StmtDeclAssign node) {

    }

    @Override
    public void visit(StmtProcedureCall node) {

    }

    @Override
    public void visit(StmtIf node) throws SemanticErrorException {
        TypeT gt = node.getGuard().getTypeCheckType();
        if (gt instanceof TypeTTauBool) {
                node.setRet(TypeR.Unit);
        }
        else {
            throw new SemanticErrorException("Guard of if statement must be a " +
                    "boolean", node.left, node.right);
        }

    }

    @Override
    public void visit(StmtIfElse node) throws SemanticErrorException {
        TypeT gt = node.getGuard().getTypeCheckType();
        if (gt instanceof TypeTTauBool) {
            TypeR s1r = node.getThenStmt().getRet();
            TypeR s2r = node.getElseStmt().getRet();
            TypeR ret = (s1r.equals(TypeR.Void) && s2r.equals(TypeR.Void)) ?
                    TypeR.Void : TypeR.Unit;
            node.setRet(ret);
        }
        else {
            throw new SemanticErrorException("Guard of if-else statement must be " +
                    "a boolean", node.left, node.right);
        }
    }

    @Override
    public void visit(StmtWhile node) throws SemanticErrorException {
        TypeT gt = node.getGuard().getTypeCheckType();
        if (gt instanceof TypeTTauBool) {
            node.setRet(TypeR.Unit);
        }
        else {
            throw new SemanticErrorException("Guard of while statement must be a " +
                    "boolean", node.left, node.right);
        }

    }

    @Override
    public void visit(StmtBlock node) throws SemanticErrorException {
        symTable.enterScope();
        List<Stmt> statements = node.getStatments();
        for (int i=0; i < statements.size() - 1; i++) {
            Stmt s = statements.get(i);
            TypeR st = s.getRet();
            if (!st.equals(TypeR.Unit)) {
                Stmt nexts = statements.get(i+1);
                throw new SemanticErrorException("Unreachable statement",
                        nexts.left, nexts.right);
            }
        }
        TypeR lst = node.getLastStatement().getRet();
        node.setRet(lst);
        symTable.exitScope();
    }

    @Override
    public void visit(FileProgram node) throws SemanticErrorException {
        symTable.enterScope();
        List<UseInterface> imports = node.getImports();
        List<FuncDefn> defns = node.getFuncDefns();
        for (UseInterface import_node : imports) {
            import_node.accept(this);
        }
        for (FuncDefn defn : defns) {
            Pair<String, TypeSymTable> signature = defn.getSignature();
            if (symTable.contains(signature.part1())) {
                throw new SemanticErrorException(
                        "Function with name " + signature.part1()
                                + " already exists",
                        defn.getLeft(), defn.getRight());
            } else {
                symTable.add(signature.part1(), signature.part2());
            }
        }
        symTable.exitScope();
    }

    @Override
    public void visit(FileInterface node) throws SemanticErrorException {
        //note: visitor will only visit program file or interface file
        List<FuncDecl> decls = node.getFuncDecls();
        for (FuncDecl decl : decls) {
            Pair<String, TypeSymTable> signature = decl.getSignature();
            if (symTable.contains(signature.part1())) {
                throw new SemanticErrorException(
                        "Function with name " + signature.part1()
                                + " already exists",
                        decl.getLeft(), decl.getRight());
            } else {
                symTable.add(signature.part1(), signature.part2());
            }
        }
    }

    @Override
    public void visit(FuncDefn node) throws SemanticErrorException {
        // for TC function body only, signatures are checked at the top-level
        symTable.enterScope();
        symTable.add(RETURN_KEY, new TypeSymTableReturn(node.getOutput()));
        for (Pair<String, TypeTTau> param : node.getParams()){
            if (symTable.contains(param.part1())) {
                throw new SemanticErrorException(
                        "No shadowing allowed in function params",
                        node.getLeft(), node.getRight());
            } else {
                symTable.add(param.part1(), new TypeSymTableVar(param.part2()));
            }
        }
        node.getBody().accept(this);
        symTable.exitScope();
    }

    @Override
    public void visit(FuncDecl node) { // do nothing
    }

    @Override
    public void visit(UseInterface node) {
        //TODO checking and parsing interface
    }
}