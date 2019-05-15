package kc875.asm;

import kc875.utils.XiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ASMUtils {

    /**
     * Returns true if instruction ins is a function label.
     *
     * @param ins instruction to test.
     */
    private static boolean instrIsFunction(ASMInstr ins) {
        return ins instanceof ASMInstrLabel
                && XiUtils.isFunction(((ASMInstrLabel) ins).getName());
    }

    /**
     * Executes function f for each function in the list of instructions ins.
     * The input instrs is not changed. The result of f on each function is
     * what replaces the function f instructions in instrs.
     *
     * @param instrs instructions.
     * @param f      function to apply on each ir function.
     */
    public static List<ASMInstr> execPerFunc(
            List<ASMInstr> instrs, Function<List<ASMInstr>, List<ASMInstr>> f
    ) {
        List<ASMInstr> optimInstrs = new ArrayList<>();
        for (int i = 0; i < instrs.size(); ) {
            ASMInstr insi = instrs.get(i);
            if (instrIsFunction(insi)) {
                int startFunc = i, endFunc = i + 1;
                for (int j = startFunc + 1; j < instrs.size(); ++j) {
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

    /**
     * Executes consumer c for each function in the list of instructions ins.
     * The input instrs is not changed.
     *
     * @param instrs instructions.
     * @param c      consumer to apply on each ASM function.
     */
    public static void execPerFunc(
            List<ASMInstr> instrs, Consumer<List<ASMInstr>> c
    ) {
        for (int i = 0; i < instrs.size(); ) {
            ASMInstr insi = instrs.get(i);
            if (instrIsFunction(insi)) {
                int startFunc = i, endFunc = i + 1;
                for (int j = startFunc + 1; j < instrs.size(); ++j) {
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
                c.accept(instrs.subList(startFunc, endFunc));
                i = endFunc;
            } else {
                // instruction not a function
                i++;
            }
        }
    }
}
