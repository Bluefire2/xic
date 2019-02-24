package ast;

import lexer.XiLexer;
import lexer.XiTokenFactory;
import polyglot.util.Pair;
import symboltable.*;
import xic_error.SemanticError;
import xic_error.SemanticTypeCheckError;
import xic_error.SemanticUnresolvedNameError;
import xic_error.LexicalError;
import xic_error.SyntaxError;
import xi_parser.XiParser;

import java.io.FileReader;
import java.nio.file.Paths;
import java.util.List;

public class VisitorTypeCheck implements VisitorAST {
    private SymbolTable symTable;
    private String sourcepath;
    private String RETURN_KEY = "__return__";

    public VisitorTypeCheck(SymbolTable symTable, String sourcepath){
        this.symTable = symTable;
        this.sourcepath = sourcepath;
    }

    public SymbolTable getSymTable() {
        return symTable;
    }

    /**
     * Throws SemanticErrorException for binary op AST node. Helper function
     * to visit(BinopExpr node).
     * @param node that results in a type checking error.
     */
    private void throwSemanticErrorBinopVisit(ExprBinop node)
            throws SemanticError {
        TypeT lType = node.getLeftExpr().getTypeCheckType();
        TypeT rType = node.getRightExpr().getTypeCheckType();
        throw new SemanticError(
                String.format("Operator %s cannot be applied to %s and %s",
                        node.opToString(), lType.toString(), rType.toString()),
                node.getLocation()
        );
    }

    @Override
    public void visit(ExprBinop node) {
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
                throwSemanticErrorBinopVisit(node);
            case MINUS:
            case MULT:
            case HI_MULT:
            case DIV:
            case MOD:
                if (lTypeIsInt && rTypeIsInt) {
                    node.setTypeCheckType(new TypeTTauInt());
                    break;
                }
                throwSemanticErrorBinopVisit(node);
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
                throwSemanticErrorBinopVisit(node);
            case GT:
            case LT:
            case GTEQ:
            case LTEQ:
                if (lTypeIsInt && rTypeIsInt) {
                    node.setTypeCheckType(new TypeTTauBool());
                    break;
                }
                throwSemanticErrorBinopVisit(node);
            case AND:
            case OR:
                if (lTypeIsBool && rTypeIsBool) {
                    node.setTypeCheckType(new TypeTTauBool());
                    break;
                }
                throwSemanticErrorBinopVisit(node);
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

                // outTypes being equal to TypeTUnit or not doesn't make a
                // difference in the resulting type of this function/procedure.
                // Function types are exactly the same, procedures just have
                // an extra context return
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
                            Expr ei = funcArgs.get(i);
                            TypeTTau ti = inTauList.get(i);
                            if (!ei.getTypeCheckType().equals(ti)) {
                                // Gamma |- ei : tj and tj != ti
                                throw new SemanticTypeCheckError(
                                        ti,
                                        ei.getTypeCheckType(),
                                        ei.getLocation()
                                );
                            }
                        }
                        // func args and func sig match
                        node.setTypeCheckType(outTypes);
                    } else {
                        // num arguments not equal
                        throw new SemanticError(
                                String.format("%d arguments expected, but" +
                                        " %d given", inTauList.size(),
                                        funcArgs.size()
                                ),
                                node.getLocation()
                        );
                    }
                }
            } else {
                throw new SemanticError(
                        String.format("%s is not a function", name),
                        node.getLocation()
                );
            }
        } catch (NotFoundException e) {
            throw new SemanticUnresolvedNameError(name, node.getLocation());
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
            throw new SemanticUnresolvedNameError(name, node.getLocation());
        }
    }

    @Override
    public void visit(ExprIndex node) {
        Expr array = node.getArray();
        Expr idx = node.getIndex();
        TypeT at = array.getTypeCheckType();
        TypeT it = idx.getTypeCheckType();
        if (at instanceof TypeTTauArray) {
            if (it instanceof TypeTTauInt) {
                node.setTypeCheckType(((TypeTTauArray) at).getTypeTTau());
            } else {
                throw new SemanticTypeCheckError(new TypeTTauInt(), it,
                        idx.getLocation());
            }
        } else {
            throw new SemanticError(
                    String.format("Cannot index type %s", idx.getE_type()),
                    array.getLocation());
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
            throw new SemanticError("Cannot apply length on non-array " +
                    "type", node.getLocation());
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
            Expr initContent = contents.get(0);
            TypeT initT = initContent.getTypeCheckType();
            if (initT instanceof TypeTTau) {
                TypeTTau initTau = (TypeTTau) initT;
                for (Expr e : contents) {
                    TypeTTau eTau = (TypeTTau) e.getTypeCheckType();
                    if (!initTau.equals(eTau)) {
                        throw new SemanticTypeCheckError(initTau, eTau,
                                e.getLocation());
                    }
                }
                // all taus are equal
                node.setTypeCheckType(new TypeTTauArray(initTau));
            } else {
                throw new SemanticError("Invalid type",
                        initContent.getLocation());
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
                throw new SemanticTypeCheckError(new TypeTTauBool(), et,
                        node.getLocation());
            case UMINUS:
                if (et instanceof TypeTTauInt) {
                    node.setTypeCheckType(new TypeTTauInt());
                    break;
                }
                throw new SemanticTypeCheckError(new TypeTTauBool(), et,
                        node.getLocation());
        }
    }

    @Override
    public void visit(AssignableIndex node) {
        if (node.getIndex() instanceof ExprIndex) {
            ExprIndex ei = (ExprIndex) node.getIndex();
            Expr array = ei.getArray();
            Expr index = ei.getIndex();

            if (array instanceof ExprId) {
                ExprId id = (ExprId) array;
                try {
                    symTable.lookup(id.getName());
                } catch (NotFoundException e) {
                    throw new SemanticUnresolvedNameError(id.getName(),
                            node.getLocation());
                }
            } else if (!(array instanceof ExprIndex)) {
                throw new SemanticError(
                        String.format("Cannot index type %s", array.getE_type()),
                        node.getLocation()
                );
            }

            if (!(index.getTypeCheckType() instanceof TypeTTauInt)) {
                throw new SemanticTypeCheckError(new TypeTTauInt(),
                        index.getTypeCheckType(), index.getLocation());
            }
        } else {
            throw new SemanticError(
                    String.format("Cannot index type %s", node.getIndex().getE_type()),
                    node.getLocation()
            );
        }
    }

    @Override
    public void visit(AssignableUnderscore node) { //Do nothing
    }

    @Override
    public void visit(AssignableId node) {
        String id = node.getId().getName();
        try {
            symTable.lookup(id);
        }
        catch (NotFoundException e) {
            throw new SemanticError("Uninitialized identifier " + id,
                    node.getLocation());
        }
    }

    @Override
    public void visit(StmtReturn node) {

    }

//    private TypeT unrollAssignableIndex(Expr index) throws UnresolvedNameException {
//        int depth = 0;
//        while (!(index instanceof ExprId)) {
//            depth++;
//            index = ((ExprIndex) index).getArray();
//        }
//        String name = ((ExprId) index).getName();
//        try {
//            TypeSymTable t = symTable.lookup(name);
//            TypeTTau tTau = ((TypeSymTableVar) t).getTypeTTau();
//            TypeTTauArray tTauArray = (TypeTTauArray) tTau;
//            // TODO: finish this
//            // check if depth of array type is equal to traversed depth
//            // if it is, return the type T inside the array
//            // otherwise return a new array type with reduced depth
//        } catch (NotFoundException e) {
//            throw new UnresolvedNameException(name, index.left, index.right);
//        }
//    }

    @Override
    public void visit(StmtAssign node) {
        List<Assignable> lhs = node.getLhs();
        List<Expr> rhs = node.getRhs();

        if (rhs.size() != 1 && rhs.size() != lhs.size()) {
            // TODO: illegal state
        } else if (rhs.size() == lhs.size()) {
            for (int i = 0; i < rhs.size(); i++) {
                Assignable currentAssignable = lhs.get(i);
                Expr currentExpression = rhs.get(i);
                TypeT givenType = currentExpression.getTypeCheckType();

                if (currentAssignable instanceof AssignableId) {
                    String name = ((AssignableId) currentAssignable).getId().getName();
                    try {
                        TypeSymTable tA = symTable.lookup(name);
                        if (tA instanceof TypeSymTableVar) {
                            TypeT expected = ((TypeSymTableVar) tA).getTypeTTau();
                            if (!givenType.subtypeOf(expected)) {
                                // TODO: what should be error location?
                                throw new SemanticTypeCheckError(expected,
                                        givenType, node.getLocation());
                            }
                        } else {
                            // TODO: illegal state
                        }
                    } catch (NotFoundException e) {
                        throw new SemanticUnresolvedNameError(name,
                                node.getLocation());
                    }
                } else if (currentAssignable instanceof AssignableIndex) {
                    AssignableIndex ai = (AssignableIndex) currentAssignable;
                    // the index must be ExprIndex
                    ExprIndex index = (ExprIndex) ai.getIndex();
                    Expr array = index.getArray();
                    Expr nextIndex = index.getIndex();
                    TypeT expectedType = array.getTypeCheckType();

                    // TODO: check if givenType is a subtype of expectedType
                    // visualised:
                    // x[][][][] = e
                    // this means that we require the type of e to be the type of x[][][] (with one [] removed)
                } else {
                    // underscore
                }
            }
        } else {

        }
    }

    @Override
    public void visit(StmtDecl node) {
        for (TypeDeclVar d : node.getDecls()) {
            TypeSymTableVar dt = new TypeSymTableVar((TypeTTau) d.typeOf());
            for (String did : d.varsOf()) {
                if (symTable.contains(did)) {
                    throw new SemanticError(
                            "Variable with name " + did
                                    + " already exists",
                            node.getLocation());
                } else {
                    symTable.add(did, dt);
                }
            }
        }
    }

    @Override
    public void visit(StmtDeclAssign node) {

    }

    @Override
    public void visit(StmtProcedureCall node) {
        try {
            TypeSymTable prType = symTable.lookup(node.getName());
            if (prType instanceof TypeSymTableFunc) {
                TypeSymTableFunc prFunc = (TypeSymTableFunc) prType;
                TypeT prInputs = prFunc.getInput();
                TypeT prOutput = prFunc.getOutput();
                List<Expr> args = node.getArgs();
                if(!(prOutput instanceof TypeTUnit)) {
                    throw new SemanticError("Not a procedure", node.getLocation());
                }
                //no parameters
                if (prInputs instanceof TypeTUnit) {
                    if (args.size() > 0) {
                        throw new SemanticError("Mismatched number of arguments", node.getLocation());
                    }
                }
                //one parameter
                else if (prInputs instanceof TypeTTau) {
                    if (!(args.size() == 1 && args.get(0).getTypeCheckType().subtypeOf(prInputs))) {
                        throw new SemanticError("Mismatched argument type", node.getLocation());
                    }
                }
                //multiple parameters
                else if (prInputs instanceof TypeTList) {
                    List<TypeTTau> inputList = ((TypeTList) prInputs).getTTauList();
                    if (args.size() != inputList.size()) {
                        throw new SemanticError("Mismatched number of arguments", node.getLocation());
                    }
                    for (int i = 0; i < args.size(); i++) {
                        if (!(args.get(i).getTypeCheckType().subtypeOf(inputList.get(i)))) {
                            throw new SemanticError("Mismatched argument type", node.getLocation());
                        }
                    }
                }
                else {
                    // TODO: Illegal state
                }
                node.setTypeCheckType(TypeR.Unit);
            }
            else {
                throw new SemanticError(node.getName() + " is not a function", node.getLocation());
            }
        }
        catch (NotFoundException e) {
            throw new SemanticError(node.getName() + " is not a function", node.getLocation());
        }

    }

    // TODO: is the symbol table being separated from the changes the if
    //  block would make in this node? We want to return Gamma, but are we
    //  really returning it? Same issue for other statement nodes.
    @Override
    public void visit(StmtIf node) {
        TypeT gt = node.getGuard().getTypeCheckType();
        if (gt instanceof TypeTTauBool) {
            node.setTypeCheckType(TypeR.Unit);
        } else {
            throw new SemanticError("Guard of if statement must be a " +
                    "bool", node.getLocation());
        }

    }

    @Override
    public void visit(StmtIfElse node) {
        TypeT gt = node.getGuard().getTypeCheckType();
        if (gt instanceof TypeTTauBool) {
            TypeR s1r = node.getThenStmt().getTypeCheckType();
            TypeR s2r = node.getElseStmt().getTypeCheckType();
            TypeR ret = (s1r.equals(TypeR.Void) && s2r.equals(TypeR.Void)) ?
                    TypeR.Void : TypeR.Unit;
            node.setTypeCheckType(ret);
        } else {
            throw new SemanticError(
                    "Guard of if-else statement must be a bool",
                    node.getLocation());
        }
    }

    @Override
    public void visit(StmtWhile node) {
        TypeT gt = node.getGuard().getTypeCheckType();
        if (gt instanceof TypeTTauBool) {
            node.setTypeCheckType(TypeR.Unit);
        } else {
            throw new SemanticError(
                    "Guard of while statement must be a bool",
                    node.getLocation());
        }

    }

    @Override
    public void visit(StmtBlock node) {
        symTable.enterScope();
        List<Stmt> statements = node.getStatments();
        if (statements.isEmpty()) {
            // empty block, follow through
            node.setTypeCheckType(TypeR.Unit);
        } else {
            // non-empty block, visit each statement
            for (int i = 0; i < statements.size() - 1; i++) {
                Stmt s = statements.get(i);
                TypeR st = s.getTypeCheckType();
                if (!st.equals(TypeR.Unit)) {
                    Stmt nexts = statements.get(i + 1);
                    throw new SemanticError("Unreachable statement",
                            nexts.getLocation());
                }
            }
            TypeR lst = node.getLastStatement().getTypeCheckType();
            node.setTypeCheckType(lst);
        }
        symTable.exitScope();
    }

    @Override
    public void visit(FileProgram node) {
        symTable.enterScope();
        List<UseInterface> imports = node.getImports();
        List<FuncDefn> defns = node.getFuncDefns();
        for (UseInterface import_node : imports) {
            import_node.accept(this);
        }
        for (FuncDefn defn : defns) {
            Pair<String, TypeSymTable> signature = defn.getSignature();
            if (symTable.contains(signature.part1())) {
                throw new SemanticError(
                        "Function with name " + signature.part1()
                                + " already exists",
                        defn.getLocation());
            } else {
                symTable.add(signature.part1(), signature.part2());
            }
        }
        symTable.exitScope();
    }

    @Override
    public void visit(FileInterface node) {
        //note: visitor will only visit program file or interface file
        List<FuncDecl> decls = node.getFuncDecls();
        for (FuncDecl decl : decls) {
            Pair<String, TypeSymTable> signature = decl.getSignature();
            if (symTable.contains(signature.part1())) {
                throw new SemanticError(
                        "Function with name " + signature.part1()
                                + " already exists",
                        decl.getLocation());
            } else {
                symTable.add(signature.part1(), signature.part2());
            }
        }
    }

    @Override
    public void visit(FuncDefn node) {
        // for TC function body only, signatures are checked at the top-level
        symTable.enterScope();
        symTable.add(RETURN_KEY, new TypeSymTableReturn(node.getOutput()));
        for (Pair<String, TypeTTau> param : node.getParams()){
            if (symTable.contains(param.part1())) {
                throw new SemanticError(
                        "No shadowing allowed in function params",
                        node.getLocation());
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
        String filename = node.getName() + ".ixi";
        String inputFilePath = Paths.get(sourcepath, filename).toString();
        try (FileReader fileReader = new FileReader(inputFilePath)) {
            XiTokenFactory xtf = new XiTokenFactory();
            XiLexer lexer = new XiLexer(fileReader, xtf);
            XiParser parser = new XiParser(lexer, xtf);
            FileInterface root = (FileInterface) parser.parse().value;
            root.accept(this);
        } catch (SyntaxError e) {
            //TODO should we print which file the error was in?
            throw e;
        } catch (LexicalError e) {
            throw e;
        } catch (Exception e) {
            //this would get thrown the file existed but was parsed as
            // a program file for some reason
            throw new SemanticError(
                    "Could not find interface "+node.getName(),
                    node.getLocation());
        }

    }
}