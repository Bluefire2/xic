package kc875.asm;

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

}
