package asm.visit;

import asm.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class RegAllocationNaiveVisitor extends RegAllocationVisitor {
    HashMap<String, Integer> tempMap;
    Stack<HashMap<String, Integer>> tempMapStack;

    public RegAllocationNaiveVisitor() {
        tempMap = new HashMap<>();
        tempMapStack = new Stack<>();
    }

    public List<ASMInstr> allocate(List<ASMInstr> input){
//        List<ASMInstr> instrs = new ArrayList<>();
//        int num_temps = count_temps(input);
//        instrs.add(new ASMInstr_2Arg(
//                ASMOpCode.ADD,
//                new ASMExprReg("rsp"),
//                new ASMExprConst(num_temps * 8))
//        );
//        //Unused regs - rbx, r7, r10-r15
//        //initial SP moved to rbx
//        instrs.add(new ASMInstr_2Arg(
//                ASMOpCode.MOV,
//                new ASMExprReg("rbx"),
//                new ASMExprReg("rsp"))
//        );
//        //generate code for instrs, modifying the tempMap
//        return instrs;

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
        if (i.getName().startsWith("_I")) {
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

    @Override
    public List<ASMInstr> visit(ASMInstr_1Arg i) {
        ASMExpr arg = i.getArg();
        List<ASMInstr> instrs = new ArrayList<>();
        if (arg instanceof ASMExprTemp) {
            String t = ((ASMExprTemp) arg).getName();
            HashMap<String, Integer> currMap = tempMapStack.peek();
            if (currMap.containsKey(t)) {
                // mapping to hardware stack present (t -> k_t), replace t
                // with a [rbp - k_t]
                Integer k_t = currMap.get(t);
                instrs.add(new ASMInstr_1Arg(
                        i.getOpCode(),
                        new ASMExprMem(new ASMExprBinOpAdd(
                                new ASMExprReg("rbp"),
                                new ASMExprConst(k_t)
                        ))
                ));
            } else {
                // mapping to hardware stack doesn't exist, dec rsp, create
                // mapping the stack t -> k_t, replace temp with a [rbp - k_t]
                instrs.add(new ASMInstr_2Arg(
                        ASMOpCode.SUB, new ASMExprReg("rsp"),
                        new ASMExprConst(8)
                ));
                // the size of the mapping is the number of temps on the
                // stack, so the new k_t will be at (size+1)*8 position
                // TODO: this will change if we save callee-save registers in
                //  IR to ASM translation.
                int k_t = (currMap.size() + 1) * 8;
                currMap.put(t, k_t);
                instrs.add(new ASMInstr_1Arg(
                        i.getOpCode(),
                        new ASMExprMem(new ASMExprBinOpAdd(
                                new ASMExprReg("rbp"),
                                new ASMExprConst(k_t)
                        ))
                ));
            }
        } else if (arg instanceof ASMExprMem) {
            // arg is a memory location, might contain temps inside
            // TODO
        } else {
            instrs.add(i);
        }
        return instrs;
    }

    @Override
    public List<ASMInstr> visit(ASMInstr_2Arg i) {
        return null;
    }
}
