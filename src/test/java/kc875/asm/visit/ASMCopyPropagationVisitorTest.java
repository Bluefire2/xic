package kc875.asm.visit;

import kc875.asm.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ASMCopyPropagationVisitorTest {

    @Test
    public void simpleTest0() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprTemp("d")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("d")
        ));

        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        v.run(instrs);
        System.out.println(instrs);
    }

    @Test
    public void simpleTest1() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprTemp("d")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("c")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

    @Test
    public void simpleTest2() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("d"),
                new ASMExprTemp("e")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprTemp("d")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("c")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

    @Test
    public void simpleTest3() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprReg("rax"),
                new ASMExprTemp("e")
        ));
        instrs.add(new ASMInstr_1Arg(
                ASMOpCode.IMUL,
                new ASMExprTemp("d")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprReg("rax")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("c")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

    @Test
    public void simpleTest4() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("b"),
                new ASMExprTemp("a")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("c"),
                new ASMExprTemp("b")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("a"),
                new ASMExprTemp("c")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

    @Test
    public void simpleTest5() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprReg("rax"),
                new ASMExprTemp("e")
        ));
        instrs.add(new ASMInstr_1Arg(
                ASMOpCode.CALL,
                new ASMExprName("_Iasdf")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("_RET0"),
                new ASMExprReg("rax")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("c")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

    @Test
    public void simpleTest6() {
        List<ASMInstr> instrs = new ArrayList<>();
        instrs.add(new ASMInstrLabel("_If"));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("_RET0"),
                new ASMExprReg("rdx")
        ));
        instrs.add(new ASMInstr_1Arg(
                ASMOpCode.CALL,
                new ASMExprName("_Iasdf")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.MOV,
                new ASMExprTemp("_RET0"),
                new ASMExprReg("rax")
        ));
        instrs.add(new ASMInstr_2Arg(
                ASMOpCode.ADD,
                new ASMExprTemp("c"),
                new ASMExprTemp("_RET0")
        ));

        System.out.println("before=" + instrs);
        ASMCopyPropagationVisitor v = new ASMCopyPropagationVisitor();
        instrs = v.run(instrs);
        System.out.println("after=" + instrs);
    }

}
