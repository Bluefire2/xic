package kc875.asm.visit;

import kc875.asm.*;
import kc875.ast.*;
import kc875.symboltable.TypeSymTableVar;
import kc875.symboltable.TypeSymTableFunc;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ASMDirectivesVisitor {
    private Map<String, List<String>> dispatchVectorLayouts;
    private Map<String, List<String>> objectLayouts;

    public ASMDirectivesVisitor(Map<String, List<String>> dvs,
                                Map<String, List<String>> objectLayouts) {
        dispatchVectorLayouts = dvs;
        this.objectLayouts = objectLayouts;
    }

    private String returnTypeName(TypeT type) {
        if (type instanceof TypeTList) {
            TypeTList tuple = (TypeTList) type;
            ArrayList<String> types = new ArrayList<>();
            tuple.getTTauList().forEach((t) -> types.add(returnTypeName(t)));
            return "t" + tuple.getLength() + String.join("", types);
        } else if (type instanceof TypeTUnit) { //TypeTUnit
            return "p";
        } else {
            return typeName(type);
        }
    }

    private String typeName(TypeT type) {
        if (type instanceof TypeTList) {
            TypeTList tuple = (TypeTList) type;
            ArrayList<String> types = new ArrayList<>();
            tuple.getTTauList().forEach((t) -> types.add(typeName(t)));
            return String.join("", types);
        } else if (type instanceof TypeTTauArray) {
            TypeTTauArray a = (TypeTTauArray) type;
            return "a" + typeName(a.getTypeTTau());
        } else if (type instanceof TypeTTauInt) {
            return "i";
        } else if (type instanceof TypeTTauBool) {
            return "b";
        } else if (type instanceof TypeTUnit) {
            return "";
        } else if (type instanceof TypeTTauClass) {
            TypeTTauClass c = (TypeTTauClass) type;
            return "o" + c.getName().length() + c.getName().replaceAll("_", "__");
        } else {
            throw new IllegalArgumentException("invalid type");
        }
    }

    public String globalName(String name, TypeTTau signature) {
        String newName = name.replaceAll("_", "__");
        String type = typeName(signature);
        return "_I_g_" + newName + "_" + type;
    }

    public String className(String name) {
        return name.replaceAll("_", "__");
    }

    public List<ASMInstr> generateBss(String labelName, Integer size) {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrDirective("align","8"));
        instrs.add(new ASMInstrDirective("globl",labelName));
        instrs.add(new ASMInstrLabel(labelName));
        instrs.add(new ASMInstrDirective("zero", size.toString()));
        return instrs;
    }

    //generates but does not initialize globals
    private List<ASMInstr> generateGlobalVarDirectives(FileProgram ast) {
        List<ASMInstr> instrs = new ArrayList<>();
        if (ast.getGlobalVars().size() != 0) {
            instrs.add(new ASMInstrDirective("bss"));
            for (StmtDecl d : ast.getGlobalVars()) {
                String gname;
                TypeTTau gtype;
                if (d instanceof StmtDeclSingle) {
                    gname = ((StmtDeclSingle) d).getName();
                    gtype = ((StmtDeclSingle) d).getDecl().getType();
                    instrs.addAll(generateBss(globalName(gname, gtype), 8));
                } else if (d instanceof StmtDeclAssign) {
                    List<String> names = ((StmtDeclAssign) d).getNames();
                    for (int i = 0; i < names.size(); i++) {
                        gname = names.get(i);
                        TypeT t = ((StmtDeclAssign) d).getDecls().get(i).typeOf();
                        if (t instanceof TypeTTau) {
                            gtype = (TypeTTau) t;
                            instrs.addAll(generateBss(globalName(gname, gtype),8));
                        }
                    }
                } else if (d instanceof StmtDeclMulti) {
                    List<String> names = ((StmtDeclMulti) d).varsOf();
                    for (String name : names) {
                        gname = name;
                        gtype = ((StmtDeclMulti) d).getType();
                        instrs.addAll(generateBss(globalName(gname, gtype),8));
                    }
                }
            }
            instrs.add(new ASMInstrDirective("text\n"));
        }
        return instrs;
    }

    //generates class related directives (startup funcs, vt, size)
    private List<ASMInstr> generateClassDirectives(FileProgram ast) {
        /* Example

        .section .ctors
        .align 8
        .quad _I_init_AnimationTimer
        .text

        */
        List<ASMInstr> instrs = new ArrayList<>();

        if (ast.getClassDefns().size() != 0 || ast.getGlobalVars().size() != 0) {
            instrs.add(new ASMInstrDirective("section", ".ctors"));
            instrs.add(new ASMInstrDirective("align", "8"));
            if (ast.getClassDefns().size() != 0) {
                for (ClassDefn c : ast.getClassDefns()) {
                    instrs.add(new ASMInstrDirective("quad", "_I_init_" + className(c.getName())));
                }
            }
            if (ast.getGlobalVars().size() != 0) {
                instrs.add(new ASMInstrDirective("quad", "_I_global_init"));
            }
            instrs.add(new ASMInstrDirective("text\n"));
        }

        //class size directives/labels //.bss (zero 8)
        /* Example

        	.bss
            .align 8
        .globl _I_size_AnimationTimer
        _I_size_AnimationTimer:
            .zero 8 //this gets filled in later by another function
            .text

         */
        if (ast.getClassDefns().size() != 0) {
            instrs.add(new ASMInstrDirective("bss"));
            for (ClassDefn c : ast.getClassDefns()) {
                int numFields = objectLayouts.get(c.getName()).size();
                instrs.addAll(generateBss("_I_size_"+className(c.getName()),
                        8 * (1 + numFields)));
            }
            instrs.add(new ASMInstrDirective("text\n"));
        }

        /*
            .bss
            .align 8
        .globl _I_vt_BallWidget
        _I_vt_BallWidget:
            .zero 632 //we need the vt size - how?
            .text
         */
        if (ast.getClassDefns().size() != 0) {
            instrs.add(new ASMInstrDirective("bss"));
            for (ClassDefn c : ast.getClassDefns()) {
                int dvSize = dispatchVectorLayouts.get(c.getName()).size();
                instrs.addAll(generateBss("_I_vt_"+className(c.getName()), 8 * dvSize));
            }
            instrs.add(new ASMInstrDirective("text\n"));
        }
        return instrs;
    }

    public List<ASMInstr> generateDirectives(FileProgram ast){
        List<ASMInstr> instrs = new ArrayList<>(generateClassDirectives(ast));
        instrs.addAll(generateGlobalVarDirectives(ast));
        return instrs;
    }
}
