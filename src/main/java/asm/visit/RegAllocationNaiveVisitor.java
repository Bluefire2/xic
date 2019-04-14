package asm.visit;

import asm.*;
import edu.cornell.cs.cs4120.util.InternalCompilerError;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegAllocationNaiveVisitor extends RegAllocationVisitor {
    private final HashMap<String, Integer> tempToStackAddrMap = new HashMap<>();
    private final HashMap<String, Integer> funcToMaxRetValMap = new HashMap<>();
    private boolean addComments;

    private static final Set<String> CALLER_SAVE_REGS = Stream.of(
            "r10", "r11", "rax"
    ).collect(Collectors.toSet());

    // rbp can't be used for data transfer, so not included here
    private static final Set<String> CALLEE_SAVE_REGS = Stream.of(
            "rbx", "r12", "r13", "r14", "r15"
    ).collect(Collectors.toSet());

    public RegAllocationNaiveVisitor(boolean addComment) {
        this.addComments = addComment;
    }

    /**
     * Returns true if instruction ins is a function label.
     *
     * @param ins instruction to test.
     */
    private boolean instrIsFunction(ASMInstr ins) {
        return ins instanceof ASMInstrLabel && ((ASMInstrLabel) ins).isFunction();
    }

    /**
     * Returns true if instruction ins is of the form `sub rsp, imm` where
     * imm can be any constant.
     *
     * @param ins instruction to test.
     */
    private boolean instrIsSubRSPImm(ASMInstr ins) {
        if (ins instanceof ASMInstr_2Arg) {
            ASMInstr_2Arg ins2 = (ASMInstr_2Arg) ins;
            return ins2.getOpCode() == ASMOpCode.SUB
                    && ins2.getDest().equals(new ASMExprReg("rsp"))
                    && ins2.getSrc() instanceof ASMExprConst;
        }
        return false;
    }

    /**
     * Returns a list of instructions with the repetitive `sub rsp, imm` in
     * the list of instructions func removed. The total subtraction that rsp
     * undergoes throughout the function is captured in the `enter totalSub, 0`
     * instruction, which replaces the existing `enter 0, 0` instruction. The
     * input func is not changed.
     *
     * @param func List of instructions signifying a function. The first
     *             instruction must be the function's label.
     */
    List<ASMInstr> removeRepetitiveRSPInFunc(List<ASMInstr> func) {
        // calculate the total amount of rsp subtractions in
        // input[startFunc:endFunc] (inclusive, exclusive)
        long totalSub = 0;
        for (ASMInstr ins : func) {
            if (instrIsSubRSPImm(ins)) {
                // sub rsp, imm
                ASMInstr_2Arg ins2 = (ASMInstr_2Arg) ins;
                totalSub += ((ASMExprConst) ins2.getSrc()).getVal();
            }
        }

        List<ASMInstr> reducedFunc = new ArrayList<>();
        // loop again, replace the enter, delete all sub rsp, imm
        for (ASMInstr ins : func) {
            if (ins.getOpCode() == ASMOpCode.ENTER) {
                // replace this ENTER 0, 0 with ENTER totalSub, 0
                reducedFunc.add(new ASMInstr_2Arg(
                        ASMOpCode.ENTER,
                        new ASMExprConst(totalSub),
                        new ASMExprConst(0)
                ));
            } else if (!instrIsSubRSPImm(ins)) {
                // not a sub rsp, so add to reducedFunc
                reducedFunc.add(ins);
            }
            // sub rsp, don't add this to reducedFunc
        }
        return reducedFunc;
    }

    /**
     * Returns true if expression expr is of the form `[rbp - imm]` where
     * imm can be any constant.
     *
     * @param expr expression to test.
     */
    boolean exprIsMemRBPMinusConst(ASMExpr expr) {
        if (expr instanceof ASMExprMem) {
            ASMExpr addr = ((ASMExprMem) expr).getAddr();
            if (addr instanceof ASMExprBinOpAdd) {
                // rbp - imm is represented as rbp + (-imm)
                ASMExprBinOpAdd a = (ASMExprBinOpAdd) addr;
                return a.getLeft().equals(new ASMExprReg("rbp"))
                        && a.getRight() instanceof ASMExprConst
                        && ((ASMExprConst) a.getRight()).getVal() < 0;
            }
            return false;
        }
        return false;
    }

    /**
     * If the expression expr is `[rbp - k_t]`, the returned expr is
     * `[rbp - k_t]` where k_t' = k_t + n. Otherwise, the expr is returned
     *
     * @param expr expression.
     * @param n constant to add to k_t.
     */
    private ASMExpr exprIfMemRBPMinusConstAddConst(ASMExpr expr, int n) {
        if (exprIsMemRBPMinusConst(expr)) {
            ASMExprBinOpAdd a = (ASMExprBinOpAdd) ((ASMExprMem) expr).getAddr();
            long negk_t = ((ASMExprConst) a.getRight()).getVal(); // negk_t < 0
            return new ASMExprMem(new ASMExprBinOpAdd(
                    new ASMExprReg("rbp"), new ASMExprConst(negk_t - n)
            ));
        } else {
            return expr;
        }
    }

    /**
     * Returns a list of instructions for the func where any callee regs
     * appearing in the func list of instructions are pushed at the beginning
     * and popped off the stack at the end. The order of pushing callee regs
     * is in ascending order of the reg names. Thus, r12 is pushed before rbx
     * etc.
     *
     * @param func list of instructions.
     */
    List<ASMInstr> saveCalleeRegsInFunc(List<ASMInstr> func) {
        // find all the callee regs in this function
        Set<String> usedRegs = new HashSet<>();
        for (ASMInstr instr : func) {
            // add the instruction's expr's regs to usedRegs
            if (instr instanceof ASMInstr_1Arg) {
                usedRegs.addAll(getRegsInExpr(((ASMInstr_1Arg) instr).getArg()));
            } else if (instr instanceof ASMInstr_2Arg) {
                usedRegs.addAll(getRegsInExpr(((ASMInstr_2Arg) instr).getDest()));
                usedRegs.addAll(getRegsInExpr(((ASMInstr_2Arg) instr).getSrc()));
            }
        }
        List<String> usedCalleeRegs = new ArrayList<>();
        for (String reg : CALLEE_SAVE_REGS) {
            if (usedRegs.contains(reg)) {
                usedCalleeRegs.add(reg);
            }
        }
        Collections.sort(usedCalleeRegs);   // just for predictability
        int N = usedCalleeRegs.size();

        if (N == 0) {
            // no callee regs used, return with a copy of func
            return new ArrayList<>(func);
        }
        // some callee regs used

        // add push instructions at the right place (just after enter)
        // add pop instructions at the right place (just before leave)

        // also adjust the rbp - k_t to be rbp - k_t' where k_t' = k_t + (8*N),
        // where N is the number of callee save regs used in this function.

        List<ASMInstr> updatedFunc = new ArrayList<>();
        for (ASMInstr instr : func) {
            if (instr.getOpCode() == ASMOpCode.ENTER) {
                updatedFunc.add(instr);// ENTER
                // add PUSH reg instructions sequentially
                for (String reg : usedCalleeRegs) {
                    updatedFunc.add(new ASMInstr_1Arg(
                            ASMOpCode.PUSH, new ASMExprReg(reg)
                    ));
                }
            } else if (instr.getOpCode() == ASMOpCode.LEAVE) {
                // add POP reg instructions sequentially, but in reverse order
                // reverse usedCalleeRegs. Doesn't affect other future runs
                // on the loop because ENTER and LEAVE can each exist one in
                // this function, and LEAVE appears at the end ==> so no use
                // of usedCalleeRegs order after this point.
                Collections.reverse(usedCalleeRegs);
                for (String reg : usedCalleeRegs) {
                    updatedFunc.add(new ASMInstr_1Arg(
                            ASMOpCode.POP, new ASMExprReg(reg)
                    ));
                }
                updatedFunc.add(instr);// LEAVE
            } else {
                if (instr instanceof ASMInstr_1Arg) {
                    updatedFunc.add(new ASMInstr_1Arg(
                            instr.getOpCode(),
                            exprIfMemRBPMinusConstAddConst(
                                    ((ASMInstr_1Arg) instr).getArg(), 8*N
                            )
                    ));
                } else if (instr instanceof ASMInstr_2Arg) {
                    updatedFunc.add(new ASMInstr_2Arg(
                            instr.getOpCode(),
                            exprIfMemRBPMinusConstAddConst(
                                    ((ASMInstr_2Arg) instr).getDest(), 8*N
                            ),
                            exprIfMemRBPMinusConstAddConst(
                                    ((ASMInstr_2Arg) instr).getSrc(), 8*N
                            )
                    ));
                } else {
                    // no expressions to replace
                    updatedFunc.add(instr);
                }
            }
        }
        return updatedFunc;
    }

    private List<ASMInstr> create_RETiPerFunc(List<ASMInstr> func) {
        // Go through all the calls in this function, and get the maximum
        // number of returns that any callee function would return
        int maxNumReturns = 0;
        for (ASMInstr instr : func) {
            if (instr instanceof ASMInstr_1Arg
                    && instr.getOpCode() == ASMOpCode.CALL) {
                String name = ((ASMExprName)
                        ((ASMInstr_1Arg) instr).getArg())
                        .getName();
                if (name.startsWith("_I")) {
                    // CALL function
                    maxNumReturns = Math.max(
                            maxNumReturns, ASMUtils.getNumReturns(name)
                    );
                }
            }
        }

        List<ASMInstr> updatedFunc = new ArrayList<>(func);
        // 0 -> funcLabel, 1 -> ENTER. Add the _RETi allocation after ENTER
        if (maxNumReturns > 0) {
            updatedFunc.add(2, new ASMInstr_2Arg(
                    ASMOpCode.SUB,
                    new ASMExprReg("rsp"),
                    new ASMExprConst(8 * maxNumReturns)
            ));
        }

        funcToMaxRetValMap.put(
                ((ASMInstrLabel) updatedFunc.get(0)).getName(), maxNumReturns
        );

        return updatedFunc;
    }

    /**
     * Executes function f for each function in the list of instructions ins.
     * The input instrs is not changed. The result of f on each function is
     * what replaces the function f instructions in instrs.
     *
     * @param instrs instructions.
     */
    private List<ASMInstr> execPerFunc(
            List<ASMInstr> instrs, Function<List<ASMInstr>, List<ASMInstr>> f
    ) {
        List<ASMInstr> optimInstrs = new ArrayList<>();
        for (int i = 0; i < instrs.size();) {
            ASMInstr insi = instrs.get(i);
            if (instrIsFunction(insi)) {
                int startFunc = i, endFunc = i+1;
                for (int j = startFunc+1; j < instrs.size(); ++j) {
                    ASMInstr insj = instrs.get(j);
                    if (j == instrs.size() - 1) {
                        // reached the end of the file
                        endFunc = instrs.size();
                        break;
                    } else if (instrIsFunction(insj)) {
                        endFunc = j;
                        break;
                    }
                }
                optimInstrs.addAll(f.apply(instrs.subList(startFunc, endFunc)));
                i = endFunc;
            } else {
                // instruction not a function
                optimInstrs.add(insi);
                i++;
            }
        }
        return optimInstrs;
    }

    public List<ASMInstr> allocate(List<ASMInstr> input){
        List<ASMInstr> instrs = new ArrayList<>();
        input = execPerFunc(input, this::create_RETiPerFunc);
        for (ASMInstr instr : input) {
            instrs.addAll(instr.accept(this));
        }
        // Collect all sub rsp, imm in a function in a single instr
        // TODO: activate the following lines when the repetitive RSP remover
        //  is fixed
//        List<ASMInstr> instrs = execPerFunc(
//                instrs, this::removeRepetitiveRSPInFunc
//        );
        return execPerFunc(instrs, this::saveCalleeRegsInFunc);
    }

    @Override
    public List<ASMInstr> visit(ASMInstrLabel i) {
        if (i.isFunction()) {
            // this label is a function, start a new hashmap
            tempToStackAddrMap.clear();

            // Add to hashmaps the mappings from _RETi to stack locations
            int maxNumRets = funcToMaxRetValMap.get(i.getName());
            for (int j = 0; j < maxNumRets; j++) {
                // [rbp - k_t]
                int k_t = -((tempToStackAddrMap.size() + 1) * 8);
                tempToStackAddrMap.put("_RET" + j, k_t);
            }
        }
        List<ASMInstr> l = new ArrayList<>();
        l.add(i);
        return l;
    }

    @Override
    public List<ASMInstr> visit(ASMInstr_0Arg i) {
        List<ASMInstr> l = new ArrayList<>();
        l.add(i);
        return l;
    }

    /**
     * Returns a list of available data registers (list may be empty),
     * excluding the ones in excludeRegs.
     * Postcondition:
     *  The returned list will have caller save regs (that were not excluded)
     *  followed by the callee save regs (that were not excluded). So, if the
     *  list is accessed sequentially and those regs are used, caller save regs
     *  will get priority in usage.
     *
     * @param excludeRegs registers to exclude.
     */
    private List<String> getAvailRegs(List<String> excludeRegs) {
        // copy registers
        Set<String> availCallerRegs = new HashSet<>(CALLER_SAVE_REGS);
        Set<String> availCalleeRegs = new HashSet<>(CALLEE_SAVE_REGS);

        availCallerRegs.removeAll(excludeRegs);
        availCalleeRegs.removeAll(excludeRegs);

        // create list with caller regs first, then add callee regs
        // ==> follows function's postcondition
        List<String> availRegs = new ArrayList<>(availCallerRegs);
        availRegs.addAll(availCalleeRegs);
        return availRegs;
    }

    /**
     * Returns the list of objects of class c in binop expression expr.
     *
     * @param expr the binop expression.
     * @param c the class to look for in the binop.
     */
    private List<String> getObjectsInBinOp(ASMExprBinOp expr,
                                           Class<? extends ASMExpr> c) {
        ASMExpr left = expr.getLeft();
        ASMExpr right = expr.getRight();
        List<String> objStrings = new ArrayList<>();
        // check the left for temps, then check the right
        if (c.isInstance(left)) {
            // left is an instance of class c. After casting to c, toString
            // will return the temp/reg name or expr string representation
            objStrings.add(c.cast(left).toString());
        } else if (left instanceof ASMExprBinOp) {
            objStrings.addAll(getObjectsInBinOp((ASMExprBinOp) left, c));
        }
        if (c.isInstance(right)) {
            objStrings.add(c.cast(right).toString());
        } else if (right instanceof ASMExprBinOp) {
            objStrings.addAll(getObjectsInBinOp((ASMExprBinOp) right, c));
        }
        return objStrings;
    }

    /**
     * Returns a list of registers used in ASM expression e
     *
     * @param e ASM expression.
     */
    private List<String> getRegsInExpr(ASMExpr e){
        List<String> regs = new ArrayList<>();
        if (e instanceof ASMExprBinOp) {
            regs.addAll(getObjectsInBinOp((ASMExprBinOp) e, ASMExprReg.class));
        } else if (e instanceof ASMExprReg) {
            regs.add(((ASMExprReg) e).getReg());
        } else if (e instanceof ASMExprMem) {
            regs.addAll(getRegsInExpr(((ASMExprMem) e).getAddr()));
        }
        return regs;
    }

    /**
     * Returns a mapping between temps in the memory expression m and the
     * data registers each temp can be replaced with in reg allocation.
     *
     * @param m memory expression.
     */
    private Map<String, String> getTempToRegMappingInMem(
            ASMExprMem m, List<String> excludeRegs
    ) {
        ASMExpr expr = m.getAddr();

        List<String> tmps = new ArrayList<>(); // temps in m
        if (expr instanceof ASMExprTemp) {
            tmps.add(((ASMExprTemp) expr).getName());
        } else if (expr instanceof ASMExprBinOp) {
            tmps.addAll(getObjectsInBinOp((ASMExprBinOp) expr, ASMExprTemp.class));
        }

        List<String> regs = new ArrayList<>(); // regs in m
        if (expr instanceof ASMExprReg) {
            regs.add(((ASMExprReg) expr).getReg());
        } else if (expr instanceof ASMExprBinOp) {
            regs.addAll(getObjectsInBinOp((ASMExprBinOp) expr, ASMExprReg.class));
        }

        // filter out regs from the list of available data regs
        regs.addAll(excludeRegs);
        List<String> availRegs = getAvailRegs(regs);
        if (availRegs.size() < tmps.size()) {
            // number of available regs is less than the temps in the mem,
            // throw an error
            throw new InternalCompilerError("Allocating regs naively: not " +
                    "enough regs for the temps in memory expression");
        } else {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < tmps.size(); ++i) {
                map.put(tmps.get(i), availRegs.get(i));
            }
            return map;
        }
    }

    /**
     * Returns an ASMExpr with all the temps replaced by the corresponding
     * regs, as provided in the mapping tempsToRegs.
     * Precondition: all temps in expr must have mappings in tempsToRegs.
     *
     * @param expr expression to replace.
     * @param tempsToRegs mapping from temps to regs.
     */
    private ASMExpr replaceTempsWithRegs(ASMExpr expr,
                                         Map<String, String> tempsToRegs) {
        if (expr instanceof ASMExprTemp) {
            // use the reg instead
            return new ASMExprReg(tempsToRegs.get(
                    ((ASMExprTemp) expr).getName())
            );
        } else if (expr instanceof ASMExprMem) {
            // replace temps inside the addr, and wrap in a memory expression
            return new ASMExprMem(replaceTempsWithRegs(
                    ((ASMExprMem) expr).getAddr(), tempsToRegs
            ));
        } else if (expr instanceof ASMExprBinOpAdd) {
            // visit both child and wrap inside a binop
            return new ASMExprBinOpAdd(
                    replaceTempsWithRegs(((ASMExprBinOpAdd) expr).getLeft(),
                            tempsToRegs),
                    replaceTempsWithRegs(((ASMExprBinOpAdd) expr).getRight(),
                            tempsToRegs)
            );
        } else if (expr instanceof ASMExprBinOpMult) {
            // visit both child and wrap inside a binop
            return new ASMExprBinOpMult(
                    replaceTempsWithRegs(((ASMExprBinOpMult) expr).getLeft(),
                            tempsToRegs),
                    replaceTempsWithRegs(((ASMExprBinOpMult) expr).getRight(),
                            tempsToRegs)
            );
        } else {
            // nothing to replace, return this expr
            return expr;
        }
    }

    /**
     * Returns the memory instruction for temp t, and adds any intermediate asm
     * instructions produced in this temp to mem conversion.
     *
     * @param t temporary.
     * @param instrs instructions to add to.
     */
    private ASMExprMem getMemForTemp(String t, List<ASMInstr> instrs) {
        Integer k_t;
        if (tempToStackAddrMap.containsKey(t)) {
            // mapping to hardware stack present (t -> k_t), replace t
            // with a [rbp - k_t]
            k_t = tempToStackAddrMap.get(t);
        } else {
            // mapping to hardware stack doesn't exist, dec rsp, create
            // mapping the stack t -> k_t, replace temp with a [rbp - k_t]
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.SUB, new ASMExprReg("rsp"),
                    new ASMExprConst(8)
            ));
            // the size of the mapping is the number of temps on the
            // stack, so the new k_t will be at (size+1)*8 position
            // k_t is negative because the stack grows downwards.
            k_t = -((tempToStackAddrMap.size() + 1) * 8);
            tempToStackAddrMap.put(t, k_t);
        }
        return new ASMExprMem(new ASMExprBinOpAdd(
                new ASMExprReg("rbp"), new ASMExprConst(k_t)
        ));
    }

    /**
     * Converts the temps in a memory expression to regs, and adds the
     * intermediate asm instructions to instrs.
     *
     * @param m memory expression.
     * @param instrs instructions to add to.
     */
    private ASMExpr convertTempsToRegsInMem(ASMExprMem m,
                                               List<ASMInstr> instrs, List<String> excludeRegs) {
        // Get potential mapping from temps to regs
        Map<String, String> tempsToRegs = getTempToRegMappingInMem(m, excludeRegs);

        // Add move instructions like: mov reg, [rbp + k_t] (k_t < 0)
        for (Map.Entry<String, String> entry : tempsToRegs.entrySet()) {
            // the temp t must exist in the currMap because t is
            // referenced inside this mem expression. Get the k_t
            Integer k_t = tempToStackAddrMap.get(entry.getKey());
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    new ASMExprReg(entry.getValue()),
                    new ASMExprMem(new ASMExprBinOpAdd(
                            new ASMExprReg("rbp"),
                            new ASMExprConst(k_t)
                    ))
            ));
        }

        // Replace the temps in mem with regs
        return replaceTempsWithRegs(m, tempsToRegs);
    }

    @Override
    public List<ASMInstr> visit(ASMInstr_1Arg i) {
        ASMExpr arg = i.getArg();
        List<ASMInstr> instrs = new ArrayList<>();
        if (addComments){
            instrs.add(new ASMInstrComment("                           "
                    +i.toString()));
        }
        if (arg instanceof ASMExprTemp) {
            instrs.add(new ASMInstr_1Arg(
                    i.getOpCode(),
                    getMemForTemp(((ASMExprTemp) arg).getName(), instrs)
            ));
        } else if (arg instanceof ASMExprMem) {
            // arg is a memory location, might contain temps inside
            // mapping from temps to regs
            instrs.add(new ASMInstr_1Arg(
                    i.getOpCode(),
                    convertTempsToRegsInMem((ASMExprMem) arg, instrs, new ArrayList<>())
            ));
        } else {
            // argument neither a temp or a mem expression, this instruction
            // does not change
            instrs.add(i);
        }
        return instrs;
    }

    @Override
    public List<ASMInstr> visit(ASMInstr_2Arg i) {
        ASMExpr l = i.getDest();
        ASMExpr r = i.getSrc();
        List<ASMInstr> instrs = new ArrayList<>();
        if (addComments){
            instrs.add(new ASMInstrComment("                           "
                    +i.toString()));
        }
        ASMExpr dest;
        if (l instanceof ASMExprTemp) {//if LHS is a temp it gets turned into a mem
            dest = getMemForTemp(((ASMExprTemp) l).getName(), instrs);
        } else if (l instanceof ASMExprMem) {// if LHS is a mem it gets turned into a mem
            dest = convertTempsToRegsInMem((ASMExprMem) l, instrs, new ArrayList<>());
        } else {//otherwise keep it
            dest = l;
        }
        List<String> usedRegs = new ArrayList<>(getRegsInExpr(dest));
        ASMExpr src;
        if (l instanceof ASMExprReg) {
            //if LHS is a reg and the op type allows mem as second arg,
            //then RHS can be a mem
            if (r instanceof ASMExprTemp) {
                src = getMemForTemp(((ASMExprTemp) r).getName(), instrs);
            } else if (r instanceof ASMExprMem) {
                src = convertTempsToRegsInMem((ASMExprMem) r, instrs, usedRegs);
            } else {
                src = r;
            }
        } else {
            //if LHS is not a reg then it means that it got turned into a mem
            //which means RHS cannot be turned into a mem, thus we need extra instruction
            if (r instanceof ASMExprTemp) {
                ASMExpr srcMem = getMemForTemp(((ASMExprTemp) r).getName(), instrs);
                usedRegs.addAll(getRegsInExpr(srcMem));
                List<String> availRegs = getAvailRegs(usedRegs);
                if (availRegs.size() == 0){
                    throw new InternalCompilerError("Allocating regs naively: not " +
                            "enough regs for RHS of 2 argument expr");
                }
                src = new ASMExprReg(availRegs.get(0));
                instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, src, srcMem));
                usedRegs.add(availRegs.get(0));
            } else if (r instanceof ASMExprMem) {
                ASMExpr srcMem = convertTempsToRegsInMem((ASMExprMem) r, instrs, usedRegs);
                usedRegs.addAll(getRegsInExpr(srcMem));
                List<String> availRegs = getAvailRegs(usedRegs);
                if (availRegs.size() == 0){
                    throw new InternalCompilerError("Allocating regs naively: not " +
                            "enough regs for RHS of 2 argument expr");
                }
                src = new ASMExprReg(availRegs.get(0));
                instrs.add(new ASMInstr_2Arg(ASMOpCode.MOV, src, srcMem));
                usedRegs.add(availRegs.get(0));
            } else {
                src = r;
            }
        }
        if (i.getOpCode() == ASMOpCode.MOVZX){
            List<String> availRegs = getAvailRegs(usedRegs);
            if (availRegs.size() == 0){
                throw new InternalCompilerError("Allocating regs naively: not " +
                        "enough regs for RHS of 2 argument expr");
            }
            instrs.add(new ASMInstr_2Arg(
                    i.getOpCode(),
                    new ASMExprReg(availRegs.get(0)),
                    src
            ));
            instrs.add(new ASMInstr_2Arg(
                    ASMOpCode.MOV,
                    dest,
                    new ASMExprReg(availRegs.get(0))
            ));
        } else {
            instrs.add(new ASMInstr_2Arg(
                    i.getOpCode(),
                    dest,
                    src
            ));
        }
        return instrs;
    }

}
