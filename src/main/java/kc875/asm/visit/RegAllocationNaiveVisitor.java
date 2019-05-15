package kc875.asm.visit;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import kc875.asm.*;
import kc875.utils.XiUtils;
import polyglot.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegAllocationNaiveVisitor extends RegAllocationVisitor {

    public enum NaiveSpillMode {
        Naive, //naive reg allocation
        Reserved, //r13,14,15 reserved for spilling
        Restore, //can use any register, borrowed register's contents are saved in stack
    }

    private final HashMap<String, Integer> tempToStackAddrMap = new HashMap<>();
    private final HashMap<String, Set<String>> funcToRefTempMap =
            new HashMap<>();
    private boolean addComments;

    private static final List<String> CALLER_SAVE_REGS = Arrays.asList(
            "r8", "r9", "r10", "r11", "rax","rcx", "rdx", "rsi", "rdi"
    );

    // rbp can't be used for data transfer, so not included here
    private static final List<String> CALLEE_SAVE_REGS = Arrays.asList(
            "rbx", "r12", "r13", "r14", "r15"
    );

    private NaiveSpillMode mode;

    public RegAllocationNaiveVisitor(boolean addComment, NaiveSpillMode mode) {
        this.addComments = addComment;
        this.mode = mode;
    }

    public RegAllocationNaiveVisitor(boolean addComment) {
        this.addComments = addComment;
        this.mode = NaiveSpillMode.Naive;
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
     * Given a function, create a list of instructions for the function where
     * any registers designated as callee-saved are pushed to the stack at the
     * beginning of the function, and popped from the stack at the end. The
     * order of pushing callee regs is in ascending order of the reg names.
     * Thus, r12 is pushed before rbx, etc.
     *
     * Note that the original list of instructions is not mutated.
     *
     * @param func The function.
     * @return A version of the function with all callee-saved registers saved.
     */
    List<ASMInstr> saveAllCalleeRegsInFunc(List<ASMInstr> func) {
        List<ASMInstr> updatedFunc = new ArrayList<>();
        List<String> usedCalleeRegs = new ArrayList<>(CALLEE_SAVE_REGS);
        Collections.sort(usedCalleeRegs);

        // No need to update memory references after ENTER because this is run
        // before allocating registers
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
                List<String> retCalleeRegs = new ArrayList<>(usedCalleeRegs);
                Collections.reverse(retCalleeRegs);
                for (String reg : retCalleeRegs) {
                    updatedFunc.add(new ASMInstr_1Arg(
                            ASMOpCode.POP, new ASMExprReg(reg)
                    ));
                }
                updatedFunc.add(instr);// LEAVE
            } else {
                updatedFunc.add(instr);
            }
        }

        return updatedFunc;
    }

    private List<ASMInstr> createTempSpaceOnStack(List<ASMInstr> func) {
        // Go through the func, and get all the unique temps referenced in it
        Set<String> tempsInFunc = new HashSet<>();
        for (ASMInstr instr : func) {
            if (instr instanceof ASMInstr_1ArgCall) {
                ASMInstr_1ArgCall ins1 = (ASMInstr_1ArgCall) instr;
                if (ins1.getArg() instanceof ASMExprTemp) {
                    tempsInFunc.add(((ASMExprTemp) ins1.getArg()).getName());
                }
            } else if (instr instanceof ASMInstr_1Arg) {
                ASMInstr_1Arg ins1 = (ASMInstr_1Arg) instr;
                if (ins1.getArg() instanceof ASMExprTemp) {
                    tempsInFunc.add(((ASMExprTemp) ins1.getArg()).getName());
                }
            } else if (instr instanceof ASMInstr_2Arg) {
                ASMInstr_2Arg ins2 = (ASMInstr_2Arg) instr;
                // each temp referenced in this func must be assigned to at a
                // certain point. So all unique temps exist as a destination
                if (ins2.getDest() instanceof ASMExprTemp) {
                    tempsInFunc.add(((ASMExprTemp) ins2.getDest()).getName());
                }
            }
        }
        List<ASMInstr> updatedFunc = new ArrayList<>(func);
        // replace the ENTER by the space for temps needed
        updatedFunc.set(1, new ASMInstr_2Arg(
                ASMOpCode.ENTER,
                new ASMExprConst(8 * tempsInFunc.size()),
                new ASMExprConst(0)
        ));
        funcToRefTempMap.put(
                ((ASMInstrLabel) updatedFunc.get(0)).getName(), tempsInFunc
        );

        return updatedFunc;
    }

    private Pair<Integer, Integer> getNumParamRets(List<ASMInstr> func,
                                                   int pos) {
        for (int i = pos; i < func.size(); i++){
            ASMInstr curr = func.get(i);
            if (curr instanceof ASMInstr_1ArgCall) {
                return new Pair<>(
                        ((ASMInstr_1ArgCall) curr).getNumParams(),
                        ((ASMInstr_1ArgCall) curr).getNumRets()
                );
            }
        }
        throw new InternalCompilerError("`call func` instruction not found");
    }

    private int getCallEndIndex(List<ASMInstr> func, int pos) {
        for (int i = pos; i < func.size(); i++) {
            ASMInstr curr = func.get(i);
            if (curr instanceof ASMInstrComment &&
                    ((ASMInstrComment) curr).getComment().equals("CALL_END")) {
                return i;
            }
        }
        throw new InternalCompilerError("CALL_END comment not found");
    }

    private List<ASMInstr> alignStackInFunc(List<ASMInstr> func) {
        List<ASMInstr> updatedFunc = new ArrayList<>(func);
        String funcName = ((ASMInstrLabel) updatedFunc.get(0)).getName();
        int numTemps = funcToRefTempMap.get(funcName).size();
        for (int i = 0; i < updatedFunc.size(); i++){
            ASMInstr curr = updatedFunc.get(i);
            if (curr instanceof ASMInstrComment &&
                    ((ASMInstrComment) curr).getComment().equals("CALL_START")) {
                //execute for each call
                Pair<Integer, Integer> pr = getNumParamRets(updatedFunc, i);
                int numParams = pr.part1();
                int numReturns = pr.part2();
                // stack space required for extra rets, params for the callee
                int numStackCallFunc = Math.max(numReturns - 2, 0) +
                        Math.max(numParams - (numReturns > 2 ? 5 : 6), 0);
                //assume 1 rip, 1 rbp, 5 callee-saved, temps on stack
                int totalStackSize = 1 + 1 + 5 + numTemps + numStackCallFunc;
                if (totalStackSize % 2 != 0) {//need padding
                    //replace comment CALL_START with sub rsp, 8
                    updatedFunc.set(i, new ASMInstr_2Arg(
                            ASMOpCode.SUB,
                            new ASMExprReg("rsp"),
                            new ASMExprConst(8)
                    ));
                    //replace CALL_END with instruction to free up space
                    int endCallIndex = getCallEndIndex(updatedFunc, i);
                    updatedFunc.set(endCallIndex, new ASMInstr_2Arg(
                            ASMOpCode.ADD,
                            new ASMExprReg("rsp"),
                            new ASMExprConst(8)
                    ));
                }
            }
        }
        return updatedFunc;
    }

    //add .globl prefix to funcs
    private List<ASMInstr> funcPrefix(List<ASMInstr> func) {
        List<ASMInstr> updatedFunc = new ArrayList<>(func);
        String funcName = ((ASMInstrLabel) updatedFunc.get(0)).getName();
        updatedFunc.add(0, new ASMInstrDirective("globl", funcName));
        return updatedFunc;
    }

    public List<ASMInstr> allocate(List<ASMInstr> input){
        List<ASMInstr> instrs = new ArrayList<>();
        input = ASMUtils.execPerFunc(input, this::createTempSpaceOnStack);
        input = ASMUtils.execPerFunc(input, this::saveAllCalleeRegsInFunc);
        input = ASMUtils.execPerFunc(input, this::alignStackInFunc);
        input = ASMUtils.execPerFunc(input, this::funcPrefix);
        for (ASMInstr instr : input) {
            if (instr  instanceof ASMInstrComment) {
                instrs.add(instr);
            }
            else if (instr  instanceof ASMInstrDirective) {
                instrs.add(instr);
            }
            else {
                instrs.addAll(instr.accept(this));
            }
        }
        return instrs;
    }



    @Override
    public List<ASMInstr> visit(ASMInstrLabel i) {
        if (XiUtils.isFunction(i.getName())) {
            // this label is a function, start a new hashmap
            tempToStackAddrMap.clear();

            // Add to hashmap the mappings from temps to stack locations
            List<String> tempsInFunc = new ArrayList<>(
                    funcToRefTempMap.get(i.getName())
            );

            for (String temp : tempsInFunc) {
                // [rbp - k_t]
                int k_t = -((tempToStackAddrMap.size() + 1) * 8);
                tempToStackAddrMap.put(temp, k_t);
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
        if (mode == NaiveSpillMode.Reserved) {
            List<String> availRegs = Stream.of(
                    "r13", "r14", "r15"
            ).collect(Collectors.toList());
            availRegs.removeAll(excludeRegs);
            return availRegs;
        } else {
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

    private List<String> getRegsInInstr(ASMInstr i) {
        List<String> regs = new ArrayList<>();
        if (i instanceof ASMInstr_1ArgCall) {
            regs.addAll(getRegsInExpr(((ASMInstr_1ArgCall) i).getArg()));
        } else if (i instanceof ASMInstr_1Arg) {
            regs.addAll(getRegsInExpr(((ASMInstr_1Arg) i).getArg()));
        } else if (i instanceof ASMInstr_2Arg) {
            regs.addAll(getRegsInExpr(((ASMInstr_2Arg) i).getSrc()));
            regs.addAll(getRegsInExpr(((ASMInstr_2Arg) i).getDest()));
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
        if (mode == NaiveSpillMode.Restore) {
            restoreSavedRegs(instrs, getRegsInInstr(i));
        }
        if (addComments) {
            addASMComment(instrs, i);
        }
        return instrs;
    }

    @Override
    public List<ASMInstr> visit(ASMInstr_1ArgCall i) {
        return visit((ASMInstr_1Arg) i);
    }

    @Override
    public List<ASMInstr> visit(ASMInstr_2Arg i) {
        ASMExpr l = i.getDest();
        ASMExpr r = i.getSrc();
        List<ASMInstr> instrs = new ArrayList<>();
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
            } else if (r instanceof ASMExprConst) {
                //if imm is > 64 bits, move to a register
                src = getAsmExprFromConst(r, instrs, usedRegs);
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
            } else if (r instanceof ASMExprConst) {
                //if imm is > 64 bits, move to a register
                src = getAsmExprFromConst(r, instrs, usedRegs);
            } else {
                src = r;
            }
        }
        if (i.getOpCode() == ASMOpCode.MOVZX) {
            List<String> availRegs = getAvailRegs(usedRegs);
            if (availRegs.size() == 0) {
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
        if (mode == NaiveSpillMode.Restore) {
            restoreSavedRegs(instrs, getRegsInInstr(i));
        }
        if (addComments) {
            addASMComment(instrs, i);
        }
        return instrs;
    }

    private void addASMComment(List<ASMInstr> instrs, ASMInstr i) {
        instrs.add(0, new ASMInstrComment("                           " + i.toString()));
    }

    private void restoreSavedRegs(List<ASMInstr> instrs, List<String> regsInInstr) {
        Set<String> saveRegs = new HashSet<>();
        for (ASMInstr instr : instrs) {
            saveRegs.addAll(getRegsInInstr(instr));
        }
        saveRegs.removeAll(regsInInstr);
        //saveRegs is the set of registers we need to save/restore
        for (String reg : saveRegs) {
            //add(0) means the pushes and pops will be in reversed order from each other
            instrs.add(0, new ASMInstr_1Arg(ASMOpCode.PUSH, new ASMExprReg(reg)));
            instrs.add(new ASMInstr_1Arg(ASMOpCode.POP, new ASMExprReg(reg)));
        }
    }

    private ASMExpr getAsmExprFromConst(ASMExpr r, List<ASMInstr> instrs, List<String> usedRegs) {
        ASMExpr src;
        long v = ((ASMExprConst) r).getVal();
        if (v > Integer.MAX_VALUE || v < Integer.MIN_VALUE) {
            List<String> availRegs = getAvailRegs(usedRegs);
            if (availRegs.size() == 0) {
                throw new InternalCompilerError("Allocating regs naively: not " +
                        "enough regs for RHS of 2 argument expr");
            }
            src = new ASMExprReg(availRegs.get(0));
            instrs.add(new ASMInstr_2Arg(ASMOpCode.MOVABS, src, r));
            usedRegs.add(availRegs.get(0));
        } else {
            src = r;
        }
        return src;
    }

}
