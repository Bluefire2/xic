package ast;

import lexer.XiLexer;
import lexer.XiTokenFactory;
import polyglot.util.Pair;
import symboltable.*;
import xi_parser.XiParser;
import xic_error.*;

import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
                throw new SemanticError(
                        String.format("%s is not a variable", name),
                        node.getLocation()
                );
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
    public void visit(ExprArrayLiteral node) {
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
    public void visit(AssignableIndex node) { }

    @Override
    public void visit(AssignableUnderscore node) { }

    @Override
    public void visit(AssignableId node) { }

    @Override
    public void visit(StmtReturn node) {
        try {
            TypeSymTableReturn t = (TypeSymTableReturn) symTable.lookup(RETURN_KEY);
            TypeT expectedReturnType = t.getReturnType();
            List<Expr> returnVals = node.getReturnVals();
            if (returnVals.size() == 0) {
                // procedure return, function must return unit
                if (!(expectedReturnType instanceof TypeTUnit)) {
                    throw new SemanticTypeCheckError(new TypeTUnit(), expectedReturnType, node.getLocation());
                }
            } else if (returnVals.size() == 1) {
                // function return, function must return a single expression of the correct type
                TypeT givenReturnType = node.getReturnVals().get(0).getTypeCheckType();
                if (!givenReturnType.subtypeOf(expectedReturnType)) {
                    throw new SemanticTypeCheckError(expectedReturnType, givenReturnType, node.getLocation());
                }
            } else {
                TypeTList expectedReturnTypes = (TypeTList) expectedReturnType;
                if (returnVals.size() != expectedReturnTypes.getLength()) {
                    throw new SemanticError(
                            String.format("%d return values expected, but got %d",
                                    expectedReturnTypes.getLength(), returnVals.size()),
                            node.getLocation());
                }
                Iterator<Expr> exprIterator = returnVals.iterator();
                Iterator<TypeTTau> typeTIterator = expectedReturnTypes.getTTauList().iterator();
                while (exprIterator.hasNext() && typeTIterator.hasNext()) {
                    TypeT currentGivenType = exprIterator.next().getTypeCheckType();
                    TypeTTau currentExpectedType = typeTIterator.next();
                    if (currentGivenType.subtypeOf(currentExpectedType)) {
                        throw new SemanticTypeCheckError(currentExpectedType, currentGivenType, node.getLocation());
                    }
                }
            }
        } catch (NotFoundException | ClassCastException e) {
            // TODO: illegal state
        }
    }

    @Override
    public void visit(StmtAssign node) {
        Assignable lhs = node.getLhs();
        Expr rhs = node.getRhs();
        TypeT givenType = rhs.getTypeCheckType();
        TypeT expectedType;

        if (lhs instanceof AssignableId) {
            AssignableId aid = (AssignableId) lhs;
            ExprId id = aid.getId();
            expectedType = id.getTypeCheckType();

        } else if (lhs instanceof AssignableIndex) {
            AssignableIndex ai = (AssignableIndex) lhs;
            ExprIndex index = (ExprIndex) ai.getIndex();
            // type of LHS index is already pre-calculated
            expectedType = index.getTypeCheckType();
        } else { //underscore
            //if LHS is underscore, RHS must be function call
            if (!(rhs instanceof ExprFunctionCall)){
                throw new SemanticError(
                        "Expected function call", node.getLocation());
            }
            expectedType = new TypeTUnit();
        }
        if (!givenType.subtypeOf(expectedType)){
            throw new SemanticTypeCheckError(expectedType, givenType, node.getLocation());
        }
    }

    @Override
    public void visit(StmtDecl node) {
        TypeDeclVar d = node.getDecl();
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

    @Override
    public void visit(StmtDeclAssign node) {
        List<TypeDecl> decls = node.getDecls();
        Expr rhs = node.getRhs();

        // mwahaha what the fuck is this
        List<Pair<String, TypeT>> vars = decls
                .stream()
                .flatMap(decl -> {
                    TypeT type = decl.typeOf();
                    return decl
                            .varsOf()
                            .stream()
                            .map(var -> new Pair<>(var, type));
                })
                .collect(Collectors.toList());

        if (vars.size() > 1) {
            try {
                // multi-assign with function that returns multiple things
                ExprFunctionCall fnCall = (ExprFunctionCall) rhs;
                TypeT output = fnCall.getTypeCheckType();

                //TODO: need to consider the inputs to the function as well!
                //ex: if f : unit -> int, then x:int = f(3,2) is invalid
                if (output instanceof TypeTList) {
                    TypeTList outputList = (TypeTList) output;
                    List<TypeTTau> givenTypes = outputList.getTTauList();
                    List<TypeT> expectedTypes = vars
                            .stream()
                            .map(Pair::part2)
                            .collect(Collectors.toList());

                    if (givenTypes.size() == expectedTypes.size()) {
                        for (int i = 0; i < givenTypes.size(); i++) {
                            TypeT expected = expectedTypes.get(i);
                            TypeT given = givenTypes.get(i);
                            if (!given.subtypeOf(expected)) {
                                throw new SemanticTypeCheckError(expected, given, node.getLocation());
                            }
                        }
                    } else {
                        throw new SemanticError(
                                String.format("%d return values expected, but got %d", expectedTypes.size(), givenTypes.size()),
                                node.getLocation()
                        );
                    }
                } else {
                    throw new SemanticError(
                            "Expected multiple return values, but only got one or none",
                            node.getLocation()
                    );
                }
            } catch (ClassCastException e) {
                throw new SemanticError("Expected callable value here", node.getLocation());
            }
        } else {
            TypeDecl d = decls.get(0);
            if (d instanceof TypeDeclVar) {
                String varName = d.varsOf().get(0);
                TypeT varType = d.typeOf();

                // check that the var isn't already declared in the context
                if (symTable.contains(varName)) {
                    throw new SemanticError(
                            String.format("Identifier %s has already been declared in this scope", varName),
                            node.getLocation()
                    )
                } else {
                    // we can safely cast because variables have to be TypeTTau
                    symTable.add(varName, new TypeSymTableVar((TypeTTau) varType));
                }

                // check that the type of the expression fits the type of the var
                if (!rhs.getTypeCheckType().subtypeOf(varType)) {
                    throw new SemanticTypeCheckError(varType, rhs.getTypeCheckType(), node.getLocation());
                }
            } else {
                //TODO handle _ = e (should be impossible, do nothing?)
            }

        }
        node.setTypeCheckType(TypeR.Unit);
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
    public void visit(FuncDecl node) { }

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