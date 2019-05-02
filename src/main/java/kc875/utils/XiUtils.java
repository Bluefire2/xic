package kc875.utils;

public class XiUtils {
    /**
     * Returns the function name given the ABI function name.
     *
     * @param name ABI function name.
     */
    public static String fNameFromABIName(String name) {
        String nameWithout_I = name.substring(2);
        int _idx = nameWithout_I.lastIndexOf('_');
        return _idx == -1 ? nameWithout_I : nameWithout_I.substring(0, _idx);
    }

    /**
     * Returns true if name is for a non-library function, false otherwise.
     */
    public static boolean isNonLibFunction(String name) {
        return name.startsWith("_I"); // based on the ABI spec
    }

    /**
     * Returns true if name is for a library function, false otherwise.
     */
    public static boolean isLibFunction(String name) {
        return name.equals("_xi_alloc") || name.equals("_xi_out_of_bounds");
    }

    /**
     * Returns true if name is for any function, false otherwise.
     */
    public static boolean isFunction(String name) {
        return isNonLibFunction(name) || isLibFunction(name);
    }

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
        if (name.equals("_xi_array_out_of_bounds"))
            return 0;
        if (name.equals("_xi_alloc"))
            return 1;
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
        if (name.equals("_xi_array_out_of_bounds"))
            return 0;
        if (name.equals("_xi_alloc"))
            return 1;
        String sig = name.substring(name.lastIndexOf('_') + 1);
        // the number of parameters is the total number of i and b in params
        // sub the returnCount since that also gets counted in iCount and bCount
        int iCount = (int) sig.chars().filter(c -> c == 'i').count();
        int bCount = (int) sig.chars().filter(c -> c == 'b').count();
        return iCount + bCount - getNumReturns(name);
    }
}
