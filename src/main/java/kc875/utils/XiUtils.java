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
        return name.equals("_I_global_init") || (
                name.startsWith("_I") && !name.startsWith("_I_size_")
                        && !name.startsWith("_I_vt") && !name.startsWith("_I_g")
        );
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

}
