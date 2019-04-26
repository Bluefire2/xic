package kc875.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ASMUtils {
    /**
     * Remove all non-digit characters from a string, and return the integer
     * value of the result.
     *
     * @param s string containing one or more digit
     * @return number contained within the string
     */
    public static int numFromString(String s) {
        return Integer.parseInt(s.replaceAll("\\D+", ""));
    }

    /**
     * Return the number of values that the function declaration returns.
     *
     * @param name name of the function from IRFuncDecl instance
     * @return number of return values
     */
    public static int getNumReturns(String name) {
        String sig = name.substring(name.lastIndexOf('_') + 1);
        if (sig.startsWith("p")) {
            // procedure
            return 0;
        } else if (sig.startsWith("t")) {
            // tuple return, get the number after "t"
            return numFromString(sig);
        } else {
            // single return
            return 1;
        }
    }

    /**
     * Return the number of parameters that the function declaration takes in.
     *
     * @param name name of the function from IRFuncDecl instance
     * @return number of parameters
     */
    public static int getNumParams(String name) {
        String sig = name.substring(name.lastIndexOf('_') + 1);
        // the number of parameters is the total number of i and b in params
        // sub the returnCount since that also gets counted in iCount and bCount
        int iCount = (int) sig.chars().filter(c -> c == 'i').count();
        int bCount = (int) sig.chars().filter(c -> c == 'b').count();
        return iCount + bCount - getNumReturns(name);
    }

    /**
     * Returns true if instruction ins is a function label.
     *
     * @param ins instruction to test.
     */
    private static boolean instrIsFunction(ASMInstr ins) {
        return ins instanceof ASMInstrLabel && ((ASMInstrLabel) ins).isFunction();
    }

    /**
     * Executes function f for each function in the list of instructions ins.
     * The input instrs is not changed. The result of f on each function is
     * what replaces the function f instructions in instrs.
     *
     * @param instrs instructions.
     */
    public static List<ASMInstr> execPerFunc(
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

}
