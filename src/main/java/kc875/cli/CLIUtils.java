package kc875.cli;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.dfa.IRGraph;
import kc875.asm.ASMInstr;
import kc875.asm.ASMInstrLabel;
import kc875.asm.ASMUtils;
import kc875.asm.dfa.ASMGraph;
import kc875.asm.dfa.LiveVariableDFA;
import kc875.cfg.DFAFramework;
import kc875.utils.XiUtils;
import polyglot.util.OptimalCodeWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class CLIUtils {
    /**
     * Writes the error message in the file.
     *
     * @param outputFilePath path of the file to write in.
     * @param errMessage     error message.
     */
    static void fileoutError(String outputFilePath, String errMessage) {
        try {
            FileWriter fw = new FileWriter(outputFilePath);
            fw.write(errMessage);
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Outputs the ir to `path_p.ir`.
     *
     * @param ir   IR.
     * @param p    optimization phase.
     * @param path path to write at.
     */
    static void fileoutIRPhase(IRNode ir, OptimPhases p, String path)
            throws Exception {
        // Get output file name and write the IR
        String fName = path + "_" + p.toString().toLowerCase() + ".ir";
        FileWriter writer = new FileWriter(fName);
        OptimalCodeWriter cw = new OptimalCodeWriter(writer, 80);
        CodeWriterSExpPrinter printer = new CodeWriterSExpPrinter(cw);
        ir.printSExp(printer);
        printer.close();
    }

    /**
     * Outputs the CFGs for all functions f in ir with paths of the form
     * `path_f_p.dot`.
     *
     * @param ir   IR to extract functions from.
     * @param p    optimization phase.
     * @param path path to write at.
     */
    static void fileoutCFGPhase(IRCompUnit ir, OptimPhases p, String path)
            throws Exception {
        // Get all functions out of ir
        for (Map.Entry<String, IRFuncDecl> f : ir.functions().entrySet()) {
            // Build the CFG for f and output to file
            IRGraph funcGraph = new IRGraph(f.getValue());
            String filename = String.format(
                    "%s_%s_%s.dot",
                    path,
                    XiUtils.fNameFromABIName(f.getKey()),
                    p.toString().toLowerCase()
            );
            funcGraph.show(filename);
        }
    }

    /**
     * Outputs the CFGs for all functions f in list of ASM instructions with
     * paths of the form `path_f_p.dot`.
     *
     * @param ins  ASM instructions to extract functions from.
     * @param p    optimization phase.
     * @param path path to write at.
     */
    static void fileoutCFGPhase(List<ASMInstr> ins, OptimPhases p, String path) {
        // Get all functions out of ir

        Consumer<List<ASMInstr>> cPerFunc = listASM -> {
            // Get name of function
            String fName = ((ASMInstrLabel) listASM.get(0)).getName();

            // Build the CFG for f and output to file
            ASMGraph funcGraph = new ASMGraph(listASM);
            String filename = String.format(
                    "%s_%s_%s.dot",
                    path,
                    XiUtils.fNameFromABIName(fName),
                    p.toString().toLowerCase()
            );
            try {
                funcGraph.show(filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        ASMUtils.execPerFunc(ins, cPerFunc);
    }

    /**
     * Outputs the CFGs for all functions f in list of ASM instructions with
     * paths of the form `path_f_p1_p2_..._pn.dot` after running DFAs p1, p2,
     * ..., pn on f.
     * Preconditions:
     * - Phases pi must not be INITIAL or FINAL.
     *
     * @param ins  ASM instructions to extract functions from.
     * @param ps   list of optimization phases, in order for running.
     * @param path path to write at.
     */
    static void fileoutCFGDFAPhase(List<ASMInstr> ins, List<OptimPhases> ps,
                                   String path) {
        // Get all functions out of ir

        Consumer<List<ASMInstr>> cPerFunc = listASM -> {
            // Get name of function
            String fName = ((ASMInstrLabel) listASM.get(0)).getName();

            // Build the CFG for f and output to file
            ASMGraph funcGraph = new ASMGraph(listASM);
            String filename = String.format(
                    "%s_%s_%s.dot",
                    path,
                    XiUtils.fNameFromABIName(fName),
                    ps.stream()
                            .map(p -> p.toString().toLowerCase())
                            .collect(Collectors.joining("_"))
            );
            try {
                // Run the DFAs specified by ps
                for (OptimPhases p : ps) {
                    DFAFramework framework;
                    switch (p) {
                        case ASMLIVEVAR:
                            framework = new LiveVariableDFA(funcGraph);
                            break;
                        default:
                            throw new IllegalAccessError(
                                    "Can't run " + p + " DFA on ASM"
                            );
                    }
                    framework.runWorklistAlgo();
                    framework.show(filename);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        ASMUtils.execPerFunc(ins, cPerFunc);
    }
}
