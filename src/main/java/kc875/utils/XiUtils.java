package kc875.utils;

public class XiUtils {
    /**
     * Returns the function name given the ABI function name.
     *
     * @param name ABI function name.
     */
    public static String fNameFromABIName(String name) {
        String nameWithout_I = name.substring(2);
        int _idx = nameWithout_I.indexOf('_');
        return _idx == -1 ? nameWithout_I : nameWithout_I.substring(0, _idx);
    }
}
