package asm.visit;

import asm.*;
import edu.cornell.cs.cs4120.util.InternalCompilerError;

import java.util.*;

public class RegAllocationNaiveVisitor extends RegAllocationVisitor {
    private Stack<HashMap<String, Integer>> tempMapStack;
    // caller-saved and callee-saved regs that can be used for data transfer
    private static final Set<String> AVAIL_DATA_REGS = Set.of(
            "rbx", "r10", "r11", "r12", "r13", "r14", "r15"
    );

    public RegAllocationNaiveVisitor() {
        tempMapStack = new Stack<>();
    }

    public List<ASMInstr> allocate(List<ASMInstr> input){
        List<ASMInstr> instrs = new ArrayList<>();
        for (ASMInstr instr : input) {
            instrs.addAll(instr.accept(this));
        }
        return instrs;
    }

    public int count_temps(List<ASMInstr> input){
        int count = 0;
        for (ASMInstr instr : input) {
            if (instr instanceof ASMInstr_2Arg) {
                ASMInstr_2Arg i2a = (ASMInstr_2Arg) instr;
                if (i2a.getOpCode() == ASMOpCode.MOV) {
                    ASMExpr dest = i2a.getDest();
                    if (dest instanceof ASMExprTemp) count++;
                }
            }
        }
        return count;
    }

    @Override
    public List<ASMInstr> visit(ASMInstrLabel i) {
        if (i.isFunction()) {
            // this label is a function, push a new hashmap
            tempMapStack.push(new HashMap<>());
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
     *
     * @param excludeRegs registers to exclude.
     */
    private List<String> getAvailRegs(List<String> excludeRegs) {
        Set<String> availRegs = new HashSet<>(AVAIL_DATA_REGS); // copy
        for (String reg : excludeRegs) {
            availRegs.remove(reg);
        }
        return new ArrayList<>(availRegs);
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
        List<String> temps = new ArrayList<>();
        // check the left for temps, then check the right
        if (c.isInstance(left)) {
            // left is an instance of class c
            temps.add(((ASMExprTemp) left).getName());
        } else if (left instanceof ASMExprBinOp) {
            temps.addAll(getObjectsInBinOp((ASMExprBinOp) left, c));
        }
        if (c.isInstance(right)) {
            temps.add(((ASMExprTemp) right).getName());
        } else if (right instanceof ASMExprBinOp) {
            temps.addAll(getObjectsInBinOp((ASMExprBinOp) right, c));
        }
        return temps;
    }

    /**
     * Returns a mapping between temps in the memory expression m and the
     * data registers each temp can be replaced with in reg allocation.
     *
     * @param m memory expression.
     */
    private Map<String, String> getTempToRegMappingInMem(ASMExprMem m) {
        ASMExpr expr = m.getAddr();

        List<String> temps = new ArrayList<>(); // temps in m
        if (expr instanceof ASMExprTemp) {
            temps.add(((ASMExprTemp) expr).getName());
        } else if (expr instanceof ASMExprBinOp) {
            Class<ASMExprTemp> c = ASMExprTemp.class;
            temps.addAll(getObjectsInBinOp((ASMExprBinOp) expr, c));
        }

        List<String> regs = new ArrayList<>(); // regs in m
        if (expr instanceof ASMExprReg) {
            regs.add(((ASMExprReg) expr).getReg());
        } else if (expr instanceof ASMExprBinOp) {
            Class<ASMExprReg> c = ASMExprReg.class;
            regs.addAll(getObjectsInBinOp((ASMExprBinOp) expr, c));
        }

        // filter out regs from the list of available data regs
        List<String> availRegs = getAvailRegs(regs);
        if (availRegs.size() < temps.size()) {
            // number of available regs is less than the temps in the mem,
            // throw an error
            throw new InternalCompilerError("Allocating regs naively: not " +
                    "enough regs for the temps in memory expression");
        } else {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < temps.size(); ++i) {
                map.put(temps.get(i), availRegs.get(i));
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
            return new ASMExprBinOpAdd(
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
        HashMap<String, Integer> currMap = tempMapStack.peek();
        Integer k_t;
        if (currMap.containsKey(t)) {
            // mapping to hardware stack present (t -> k_t), replace t
            // with a [rbp - k_t]
            k_t = currMap.get(t);
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
            // TODO: this will change if we save callee-save registers in
            //  IR to ASM translation.
            k_t = -((currMap.size() + 1) * 8);
            currMap.put(t, k_t);
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
                                               List<ASMInstr> instrs) {
        HashMap<String, Integer> currMap = tempMapStack.peek();
        // Get potential mapping from temps to regs
        Map<String, String> tempsToRegs = getTempToRegMappingInMem(m);

        // Add move instructions like: mov reg, [rbp + k_t] (k_t < 0)
        for (Map.Entry<String, String> entry : tempsToRegs.entrySet()) {
            // the temp t must exist in the currMap because t is
            // referenced inside this mem expression. Get the k_t
            Integer k_t = currMap.get(entry.getKey());
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
                    convertTempsToRegsInMem((ASMExprMem) arg, instrs)
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
        return null;
    }
}
