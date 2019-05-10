package kc875.ast.visit;

import kc875.ast.*;
import kc875.lexer.XiLexer;
import kc875.lexer.XiTokenFactory;
import kc875.symboltable.*;
import kc875.xi_parser.IxiParser;
import kc875.xic_error.*;
import polyglot.util.Pair;

import java.io.FileReader;
import java.nio.file.Paths;
import java.util.*;

public class TypeCheckVisitor implements ASTVisitor<Void> {
    private SymbolTable<TypeSymTable> symTable;
    private Map<String, ClassDecl> classNameToDeclMap;
    private Map<String, TypeTTauClass> classNameToTypeMap;
    private Set<UseInterface> importedInterfaces;
    private String libpath;
    private String RETURN_KEY = "__rho__";
    private String BREAK_KEY = "__beta__";
    private String INCLASS_KEY = "__kappa__";

    public TypeCheckVisitor(SymbolTable<TypeSymTable> symTable, String libpath) {
        this.symTable = symTable;
        this.classNameToDeclMap = new HashMap<>();
        this.classNameToTypeMap = new HashMap<>();
        this.importedInterfaces = new HashSet<>();
        this.libpath = libpath;
        symTable.enterScope();
    }

    public SymbolTable getSymTable() {
        return symTable;
    }

    public Map<String, ClassDecl> getClassNameToDeclMap() {
        return classNameToDeclMap;
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

    /**
     * The lub function as specified in the type inference rule writeup.
     *
     * @param a The first result type.
     * @param b The second result type.
     * @return The value of lub(a, b).
     */
    private TypeR lub(TypeR a, TypeR b) {
        if (a == TypeR.Unit || b == TypeR.Unit) {
            return TypeR.Unit;
        } else {
            return TypeR.Void;
        }
    }

    @Override
    public Void visit(ExprBinop node) {
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
                    if (larr.equals(rarr)) {
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
                    if (lTau.equals(rTau)) {
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
        return null;
    }

    @Override
    public Void visit(ExprBoolLiteral node) {
        node.setTypeCheckType(new TypeTTauBool());
        return null;
    }

    @Override
    public Void visit(ExprFunctionCall node) {
        String name = node.getName();
        try {
            TypeSymTable t = symTable.lookup(name);
            if (!(t instanceof TypeSymTableFunc))
                throw new SemanticError(
                        String.format("%s is not a function", name),
                        node.getLocation()
                );
            // else
            TypeSymTableFunc funcSig = (TypeSymTableFunc) t;
            node.setSignature(funcSig);
            TypeT inTypes = funcSig.getInput();
            TypeT outTypes = funcSig.getOutput();
            if (outTypes instanceof TypeTUnit) {
                throw new SemanticError(
                        String.format("%s is not a function", name),
                        node.getLocation()
                );
            }

            List<Expr> args = node.getArgs();
            // outTypes being equal to TypeTUnit or not doesn't make a
            // difference in the resulting type of this function/procedure.
            // Function types are exactly the same, procedures just have
            // an extra context return
            if (inTypes instanceof TypeTUnit) {
                // function with no args
                node.setTypeCheckType(outTypes);
            } else if (inTypes instanceof TypeTTau) {
                if (args.size() != 1)
                    throw new SemanticError(
                            "Mismatched number of arguments", node.getLocation());
                // function with 1 arg
                Expr arg = args.get(0);
                if (!arg.getTypeCheckType().equals(inTypes))
                    throw new SemanticTypeCheckError(
                            inTypes,
                            arg.getTypeCheckType(),
                            arg.getLocation()
                    );
                // arg and expected param type are equal
                node.setTypeCheckType(outTypes);
            } else if (inTypes instanceof TypeTList) {
                // function with >= 2 args
                List<TypeTTau> inTauList = ((TypeTList) inTypes).getTTauList();
                if (inTauList.size() != args.size())
                    // num arguments not equal
                    throw new SemanticError(
                            "Mismatched number of arguments", node.getLocation());
                // else
                for (int i = 0; i < args.size(); ++i) {
                    Expr ei = args.get(i);
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
            }
        } catch (NotFoundException e) {
            throw new SemanticUnresolvedNameError(name, node.getLocation());
        }
        return null;
    }

    @Override
    public Void visit(ExprId node) {
        String name = node.getName();
        try {
            TypeSymTable t = symTable.lookup(name);
            if (!(t instanceof TypeSymTableVar))
                throw new SemanticError(
                        String.format("%s is not a variable", name),
                        node.getLocation()
                );
            // else
            node.setTypeCheckType(((TypeSymTableVar) t).getTypeTTau());
        } catch (NotFoundException e){
            throw new SemanticUnresolvedNameError(name, node.getLocation());
        }
        return null;
    }

    @Override
    public Void visit(ExprIndex node) {
        Expr array = node.getArray();
        Expr idx = node.getIndex();
        TypeT at = array.getTypeCheckType();
        TypeT it = idx.getTypeCheckType();
        if (!(at instanceof TypeTTauArray))
            throw new SemanticError(
                    String.format("Cannot index type %s", idx.getE_type()),
                    array.getLocation());
        // else
        if (!(it instanceof TypeTTauInt))
            throw new SemanticTypeCheckError(new TypeTTauInt(), it,
                    idx.getLocation());
        // else
        node.setTypeCheckType(((TypeTTauArray) at).getTypeTTau());
        return null;
    }

    @Override
    public Void visit(ExprIntLiteral node) {
        node.setTypeCheckType(new TypeTTauInt());
        return null;
    }

    @Override
    public Void visit(ExprLength node) {
        if (!(node.getArray().getTypeCheckType() instanceof TypeTTauArray))
            throw new SemanticError("Cannot apply length on non-array " +
                    "type", node.getLocation());
        // else
        node.setTypeCheckType(new TypeTTauInt());
        return null;
    }

    @Override
    public Void visit(ExprArrayLiteral node) {
        List<Expr> contents = node.getContents();
        int length = node.getLength();
        if (length == 0) {
            node.setTypeCheckType(new TypeTTauArray());
        } else {
            Expr initContent = contents.get(0);
            TypeT initT = initContent.getTypeCheckType();
            if (!(initT instanceof TypeTTau))
                throw new SemanticError("Invalid type",
                        initContent.getLocation());
            // else
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
        }
        return null;
    }

    @Override
    public Void visit(ExprUnop node) {
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
        return null;
    }

    @Override
    public Void visit(ExprNew node) {
        String className = node.getName();
        try {
            TypeSymTable t = symTable.lookup(className);
            if (!(t instanceof TypeSymTableClass))
                // className not in context
                throw new SemanticUnresolvedNameError(
                        className, node.getLocation()
                );
            node.setTypeCheckType(((TypeSymTableClass) t).getType());
        } catch (NotFoundException e) {
            throw new SemanticUnresolvedNameError(
                    className, node.getLocation()
            );
        }
        return null;
    }

    @Override
    public Void visit(ExprNull node) {
        try {
            TypeSymTableInClass t =
                    (TypeSymTableInClass) symTable.lookup(INCLASS_KEY);
            node.setTypeCheckType(t.getTypeTTauClass());
        } catch (NotFoundException e){
            throw new SemanticError(
                    "null not allowed outside a class definition",
                    node.getLocation()
            );
        }
        return null;
    }

    @Override
    public Void visit(ExprThis node) {
        try {
            TypeSymTableInClass t =
                    (TypeSymTableInClass) symTable.lookup(INCLASS_KEY);
            node.setTypeCheckType(t.getTypeTTauClass());
        } catch (NotFoundException e){
            throw new SemanticError(
                    "this not allowed outside a class definition",
                    node.getLocation()
            );
        }
        return null;
    }

    @Override
    public Void visit(AssignableIndex node) {
        return null;
    }

    @Override
    public Void visit(AssignableId node) {
        return null;
    }

    @Override
    public Void visit(StmtReturn node) {
        try {
            TypeSymTableReturn t = (TypeSymTableReturn) symTable.lookup(RETURN_KEY);
            TypeT expectedReturnType = t.getReturnType();
            List<Expr> returnVals = node.getReturnVals();
            if (expectedReturnType instanceof TypeTUnit) {
                if (returnVals.size() != 0) {
                    throw new SemanticError(
                            String.format("%d return values expected, but got %d",
                                    0, returnVals.size()),
                            node.getLocation());
                }
            } else if (expectedReturnType instanceof TypeTTau) {
                if (returnVals.size() != 1) {
                    throw new SemanticError(
                            String.format("%d return values expected, but got %d",
                                    1, returnVals.size()),
                            node.getLocation());
                }
                TypeT givenReturnType = returnVals.get(0).getTypeCheckType();
                if (!subTypeOf(givenReturnType, expectedReturnType)) {
                    throw new SemanticTypeCheckError(expectedReturnType,
                            givenReturnType, node.getLocation());
                }
            } else { //multiple return types
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
                    if (!subTypeOf(currentGivenType, currentExpectedType)) {
                        throw new SemanticTypeCheckError(currentExpectedType,
                                currentGivenType, node.getLocation());
                    }
                }
            }
            node.setTypeCheckType(TypeR.Void);
        } catch (NotFoundException | ClassCastException e) {
            // Illegal state
        }
        return null;
    }

    @Override
    public Void visit(StmtAssign node) {
        Assignable lhs = node.getLhs();
        Expr rhs = node.getRhs();
        TypeT givenType = rhs.getTypeCheckType();
        TypeT expectedType;

        if (lhs instanceof AssignableId) {
            AssignableId aid = (AssignableId) lhs;
            Expr i = aid.getExprId();
            expectedType = i.getTypeCheckType();
        } else if (lhs instanceof AssignableIndex) {
            AssignableIndex ai = (AssignableIndex) lhs;
            ExprIndex index = (ExprIndex) ai.getIndex();
            // type of LHS index is already pre-calculated
            expectedType = index.getTypeCheckType();
        } else {
            // TODO: can also be a field
            throw new SemanticError(
                    "Expression can't be assigned to",
                    node.getLocation()
            );
        }
        if (givenType instanceof TypeTList){
            throw new SemanticError(
                    "Mismatched number of values",
                    node.getLocation()
            );
        }
        if (!subTypeOf(givenType, expectedType)){
            throw new SemanticTypeCheckError(expectedType, givenType, node.getLocation());
        }
        node.setTypeCheckType(TypeR.Unit);
        return null;
    }

    @Override
    public Void visit(StmtDeclSingle node) {
        TypeDeclVar d = node.getDecl();
        TypeTTau t = (TypeTTau) d.typeOf();
        TypeSymTableVar dt = new TypeSymTableVar(t);
        for (String did : d.varsOf()) {
            while (t instanceof TypeTTauArray) {
                // Unwrap t to ensure the initializer lengths are ints
                TypeTTauArray tArr = (TypeTTauArray) t;
                Expr tArrSize = tArr.getSize();
                if (tArrSize != null) {
                    tArrSize.accept(this);
                    if (!tArrSize.getTypeCheckType().equals(new TypeTTauInt()))
                        // Initializer length is not int type
                        throw new SemanticError(
                                String.format("Expected int, but got %s",
                                        tArrSize.getTypeCheckType()),
                                tArrSize.getLocation()
                        );
                }
                if (tArr.getTypeTTau() == null) {
                    // Inner type not initialized, lengths will be
                    // uninitialized hereafter too
                    break;
                } else {
                    t = tArr.getTypeTTau();
                }
            }
            if (symTable.contains(did)) {
                throw new SemanticError(
                        String.format("Duplicate variable %s", did),
                        node.getLocation());
            } else {
                symTable.add(did, dt);
            }
        }
        node.setTypeCheckType(TypeR.Unit);
        return null;
    }

    /**
     * Checks that decl and givenType are compatible, and adds the binding
     * [decl.vi -> decl.typeOf()] for all vi in decl.varsOf() if
     * compatibility test passed. Throws SemanticError if not compatible.
     * @param node StmtDeclAssign node of the assignment.
     * @param decl declaration.
     * @param givenType type of the corresponding RHS function call.
     */
    private void checkDeclaration(ASTNode node, TypeDecl decl, TypeT givenType) {
        // check that the given type is compatible with the expected type
        TypeT varType = decl.typeOf();
        if (!subTypeOf(givenType, varType)) {
            throw new SemanticTypeCheckError(varType, givenType, node.getLocation());
        }
        // givenType is a subtype of varType == good
        // check that the var isn't already declared in the context
        for (String var : decl.varsOf()) {
            if (symTable.contains(var)) {
                throw new SemanticError(
                        String.format("Duplicate variable %s", var),
                        node.getLocation()
                );
            } else {
                if (varType instanceof TypeTTau) {
                    symTable.add(var, new TypeSymTableVar((TypeTTau) varType));
                }
                // else, do nothing (varType MUST be unit, underscore is not
                // put in the symTable
            }
        }
    }

    @Override
    public Void visit(StmtDeclMulti node) {
        List<String> vars = node.getVars();
        TypeTTau type = node.getType();

        for (String var : vars) {
            if (symTable.contains(var))
                throw new SemanticError(
                        String.format("Duplicate variable %s", var),
                        node.getLocation()
                );
            symTable.add(var, new TypeSymTableVar(type));
        }
        node.setTypeCheckType(TypeR.Unit);
        return null;
    }

    @Override
    public Void visit(StmtDeclAssign node) {
        List<TypeDecl> decls = node.getDecls();
        Expr rhs = node.getRhs();

        if (decls.size() > 1) {
            try {
                // multi-assign with function that returns multiple things
                ExprFunctionCall fnCall = (ExprFunctionCall) rhs;
                TypeT output = fnCall.getTypeCheckType();

                if (output instanceof TypeTList) {
                    TypeTList outputList = (TypeTList) output;
                    List<TypeTTau> givenTypes = outputList.getTTauList();

                    if (givenTypes.size() == decls.size()) {
                        for (int i = 0; i < givenTypes.size(); i++)
                            checkDeclaration(node, decls.get(i),
                                    givenTypes.get(i));
                    } else {
                        throw new SemanticError(
                                "Mismatched number of values",
                                node.getLocation()
                        );
                    }
                } else {
                    throw new SemanticError(
                            "Mismatched number of values",
                            node.getLocation()
                    );
                }
            } catch (ClassCastException e) {
                throw new SemanticError("Expected function call", node.getLocation());
            }
        } else {
            // handle type compatibility for one decl
            checkDeclaration(node, decls.get(0), rhs.getTypeCheckType());
        }
        node.setTypeCheckType(TypeR.Unit);
        return null;
    }

    @Override
    public Void visit(StmtProcedureCall node) {
        try {
            TypeSymTable prType = symTable.lookup(node.getName());
            if (prType instanceof TypeSymTableFunc) {
                TypeSymTableFunc prFunc = (TypeSymTableFunc) prType;
                node.setSignature(prFunc);
                TypeT prInputs = prFunc.getInput();
                TypeT prOutput = prFunc.getOutput();
                List<Expr> args = node.getArgs();
                if (!(prOutput instanceof TypeTUnit)) {
                    throw new SemanticError(node.getName()
                            + " is not a procedure", node.getLocation());
                }
                if (prInputs instanceof TypeTUnit) {
                    //no parameters
                    if (args.size() > 0) {
                        throw new SemanticError(
                                "Mismatched number of arguments", node.getLocation());
                    }
                } else if (prInputs instanceof TypeTTau) {
                    //one parameter
                    if (!(args.size() == 1)) {
                        throw new SemanticError(
                                "Mismatched number of arguments", node.getLocation());
                    }

                    TypeT given = args.get(0).getTypeCheckType();
                    if (!(given.equals(prInputs))) {
                        throw new SemanticTypeCheckError(prInputs, given,
                                node.getLocation());
                    }
                } else if (prInputs instanceof TypeTList) {
                    //multiple parameters
                    List<TypeTTau> inputList = ((TypeTList) prInputs).getTTauList();
                    if (args.size() != inputList.size()) {
                        throw new SemanticError(
                                "Mismatched number of arguments", node.getLocation());
                    }
                    for (int i = 0; i < args.size(); i++) {
                        Expr ei = args.get(i);
                        TypeT expected = inputList.get(i);
                        if (!ei.getTypeCheckType().equals(expected)) {
                            throw new SemanticTypeCheckError(
                                    expected,
                                    ei.getTypeCheckType(),
                                    ei.getLocation()
                            );
                        }
                    }
                }
                node.setTypeCheckType(TypeR.Unit);
            } else {
                throw new SemanticError(node.getName()
                        + " is not a procedure", node.getLocation());
            }
        } catch (NotFoundException e) {
            throw new SemanticError(node.getName()
                    + " is not a procedure", node.getLocation());
        }
        return null;
    }

    @Override
    public Void visit(StmtIf node) {
        TypeT gt = node.getGuard().getTypeCheckType();
        if (gt instanceof TypeTTauBool) {
            symTable.enterScope();
            node.getThenStmt().accept(this);
            symTable.exitScope();
            node.setTypeCheckType(TypeR.Unit);
        } else {
            throw new SemanticError("Guard of if statement must be a " +
                    "bool", node.getLocation());
        }
        return null;
    }

    @Override
    public Void visit(StmtIfElse node) {
        TypeT gt = node.getGuard().getTypeCheckType();
        if (gt instanceof TypeTTauBool) {
            symTable.enterScope();
            node.getThenStmt().accept(this);
            symTable.exitScope();
            symTable.enterScope();
            node.getElseStmt().accept(this);
            symTable.exitScope();
            TypeR s1r = node.getThenStmt().getTypeCheckType();
            TypeR s2r = node.getElseStmt().getTypeCheckType();
            if (s1r != null && s2r != null) {
                node.setTypeCheckType(lub(s1r, s2r));
            }
            else node.setTypeCheckType(TypeR.Unit);
        } else {
            throw new SemanticError(
                    "Guard of if-else statement must be a bool",
                    node.getLocation());
        }
        return null;
    }

    @Override
    public Void visit(StmtWhile node) {
        TypeT gt = node.getGuard().getTypeCheckType();
        if (gt instanceof TypeTTauBool) {
            symTable.enterScope();
            symTable.add(BREAK_KEY, new TypeSymTableUnit());
            node.getDoStmt().accept(this);
            symTable.exitScope();
            node.setTypeCheckType(TypeR.Unit);
        } else {
            throw new SemanticError(
                    "Guard of while statement must be a bool",
                    node.getLocation());
        }
        return null;
    }

    @Override
    public Void visit(StmtBlock node) {
        symTable.enterScope();
        List<Stmt> statements = node.getStatments();
        for (Stmt s : statements) {
            s.accept(this);
        }
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
        return null;
    }

    @Override
    public Void visit(StmtBreak node) {
        if (symTable.contains(BREAK_KEY))
            node.setTypeCheckType(TypeR.Void);
        else
            throw new SemanticError(
                    "break not allowed outside a loop", node.getLocation()
            );
        return null;
    }

    @Override
    public Void visit(FileProgram node) {
        List<UseInterface> imports = node.getImports();
        List<ClassDefn> classDefns = node.getClassDefns();
        List<StmtDecl> globalVars = node.getGlobalVars();
        List<FuncDefn> funcDefns = node.getFuncDefns();

        // Visit the used modules
        for (UseInterface interfaceToImport : imports) {
            if (!importedInterfaces.contains(interfaceToImport))
                // only visit the import if not visited before
                interfaceToImport.accept(this);
            importedInterfaces.add(interfaceToImport);
        }

        // TODO also visit classes etc.
        for (FuncDefn defn : funcDefns) {
            Pair<String, TypeSymTable> signature = defn.getSignature();
            TypeSymTableFunc func_sig = (TypeSymTableFunc) signature.part2();
            try {
                TypeSymTable existing = symTable.lookup(signature.part1());
                TypeSymTableFunc existingf = (TypeSymTableFunc) existing;
                //existing function has already been defined
                if (!existingf.can_decl()) {
                    throw new SemanticError(
                            String.format("Function with name %s has already been defined",signature.part1()),
                            defn.getLocation());
                }
                //existing function has different signature
                if (!(existingf.getInput().equals(func_sig.getInput()) &&
                        existingf.getOutput().equals(func_sig.getOutput()))) {
                    throw new SemanticError(
                            String.format("Existing function with name %s has different signature", signature.part1()),
                            defn.getLocation());
                }
                existingf.set_can_decl(false);
            } catch (NotFoundException e) {
                //func_sig is already set to not re-declarable
                symTable.add(signature.part1(), func_sig);
            }
        }
        return null;
    }

    @Override
    public Void visit(FileInterface node) {
        //note: visitor will only visit program file or interface file
        List<FuncDecl> decls = node.getFuncDecls();

        // TODO also visit classes etc.
        for (FuncDecl decl : decls) {
            Pair<String, TypeSymTable> signature = decl.getSignature();
            TypeSymTableFunc func_sig = (TypeSymTableFunc) signature.part2();
            try {
                TypeSymTable existing = symTable.lookup(signature.part1());
                TypeSymTableFunc existingf = (TypeSymTableFunc) existing;
                //do not check if re-declarable bc imports come before defns

                //existing function has different signature
                if (!(existingf.getInput().equals(func_sig.getInput()) &&
                        existingf.getOutput().equals(func_sig.getOutput()))) {
                    throw new SemanticError(
                            String.format("Existing function with name %s has different signature", signature.part1()),
                            decl.getLocation());
                }
                //do nothing because function sig already exists
            } catch (NotFoundException e) {
                symTable.add(signature.part1(), signature.part2());
            }
        }
        return null;
    }

    @Override
    public Void visit(FuncDefn node) {
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
        Stmt body = node.getBody();
        body.accept(this);
        if (!(node.getOutput() instanceof TypeTUnit)
                && body.getTypeCheckType() != TypeR.Void)
            // func def returns non-unit but doesn't end with a return
            throw new SemanticError("Missing return",
                    body.getLocation());
        symTable.exitScope();
        return null;
    }

    // TODO
    @Override
    public Void visit(FuncDecl node) {
        return null;
    }

    // TODO
    @Override
    public Void visit(ClassDecl node) {
        return null;
    }

    // TODO
    @Override
    public Void visit(ClassDefn node) {
        return null;
    }

    @Override
    public Void visit(UseInterface node) {
        String filename = node.getName() + ".ixi";
        String inputFilePath = Paths.get(libpath, filename).toString();
        try (FileReader fileReader = new FileReader(inputFilePath)) {
            XiTokenFactory xtf = new XiTokenFactory();
            XiLexer lexer = new XiLexer(fileReader, xtf);
            IxiParser parser = new IxiParser(lexer, xtf);
            FileInterface root = (FileInterface) parser.parse().value;
            if (root != null) {
                root.accept(this);
            }
        } catch (SyntaxError | LexicalError e) {
            e.stdoutError(inputFilePath);
            throw new SemanticError(
                    "Faulty interface file " + filename,
                    node.getLocation()
            );
        } catch (Exception e) {
            //this would get thrown the file existed but was parsed as
            // a program file for some reason
            throw new SemanticError(
                    "Could not find interface "+node.getName(),
                    node.getLocation());
        }
        return null;
    }

    // sub typing rules
    private boolean subTypeOf(TypeT fst, TypeT snd) {
        if (fst instanceof TypeTList)
            return subTypeOf((TypeTList) fst, snd);
        if (fst instanceof TypeTTau)
            return subTypeOf((TypeTTau) fst, snd);
        if (fst instanceof TypeTUnit)
            return subTypeOf((TypeTUnit) fst, snd);
        throw new IllegalAccessError("Illegal use of subTypeOf");
    }

    private boolean subTypeOf(TypeTTau fst, TypeT snd) {
        if (fst instanceof TypeTTauArray)
            return subTypeOf((TypeTTauArray) fst, snd);
        if (fst instanceof TypeTTauBool)
            return subTypeOf((TypeTTauBool) fst, snd);
        if (fst instanceof TypeTTauClass)
            return subTypeOf((TypeTTauClass) fst, snd);
        if (fst instanceof TypeTTauInt)
            return subTypeOf((TypeTTauInt) fst, snd);
        throw new IllegalAccessError("Illegal use of subTypeOf");
    }

    private boolean subTypeOf(TypeTTauArray fst, TypeT snd) {
        if (snd instanceof TypeTUnit)
            return true;
        if (!(snd instanceof TypeTTauArray))
            return false;
        TypeTTauArray snd_ = (TypeTTauArray) snd;
        if (fst.getTypeTTau() == null || snd_.getTypeTTau() == null)
            return true;
        // TODO: make this invariant instead of covariant? Think about class
        //  subtyping on the taus here.
        return subTypeOf(fst.getTypeTTau(), snd_.getTypeTTau());
    }

    private boolean subTypeOf(TypeTTauBool fst, TypeT snd) {
        return snd instanceof TypeTTauBool || snd instanceof TypeTUnit;
    }

    private boolean subTypeOf(TypeTTauClass fst, TypeT snd) {
        if (snd instanceof TypeTUnit)
            // everything is a sub type of unit
            return true;

        if (!(snd instanceof TypeTTauClass))
            // if t is not unit, it must be at least a class
            return false;
        TypeTTauClass snd_ = (TypeTTauClass) snd;

        // Either this is the same as c or
        // - if sc is known, sc is a subtype of c
        // - TODO: if sc is unknown, then this is not a subtype of c
        //    (isKnown check at the end implements this)
        return fst.getName().equals(snd_.getName());
//                || fst.getSuperClass().to(
//                        sc -> subTypeOf(sc, snd_.getSuperClass())
//        ).isKnown();
    }

    private boolean subTypeOf(TypeTTauInt fst, TypeT snd) {
        return snd instanceof TypeTTauInt || snd instanceof TypeTUnit;
    }

    private boolean subTypeOf(TypeTList fst, TypeT snd) {
        if (snd instanceof TypeTUnit)
            return true;
        if (!(snd instanceof TypeTList))
            return false;
        List<TypeTTau> fstTauList = fst.getTTauList();
        List<TypeTTau> sndTauList = ((TypeTList) snd).getTTauList();

        // Check the lengths are equal
        if (fstTauList.size() != sndTauList.size())
            return false;

        // Check each tau is subtype
        for (int i = 0; i < sndTauList.size(); ++i) {
            if (!subTypeOf(fstTauList.get(i), sndTauList.get(i)))
                return false;
        }
        // All taus are subtypes of the other taus
        return true;
    }

    private boolean subTypeOf(TypeTUnit fst, TypeT snd) {
        return snd instanceof TypeTUnit;
    }

}