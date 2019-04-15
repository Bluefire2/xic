# Summary
 Test script: xthScript+O
 xic-build: OK
 Test collection: xic (Test --help)
 []: OK
 xic (Test --help): 1 out of 1 tests succeeded.
 Test collection: xic (Test --irgen)
 ex01.xi: OK
 ack.xi: OK
 primes.xi: OK
 xic (Test --irgen): 3 out of 3 tests succeeded.
Number of IRs: 3
Number of canonical IRs: 3
Number of constant-folded IRs: 3
Number of correct IRs: 3
 Test collection: xic-ref (--irgen [pretest])
 test_conv.xi: OK
 test_io.xi: OK
 xic-ref (--irgen [pretest]): 2 out of 2 tests succeeded.
Number of IRs: 2
Number of canonical IRs: 2
Number of constant-folded IRs: 2
Number of correct IRs: 2
 Test collection: xic-ref (--irgen [basic test])
 arracc01.xi: OK
 arracc02.xi: OK
 arracc03.xi: OK
 arracc04.xi: OK
 arracc05.xi: OK
 arracc06.xi: OK
 arracc07.xi: OK
 arracc08.xi: OK
 arrinit01.xi: OK
 arrinit02.xi: OK
 arrinit03.xi: OK
 arrinit04.xi: OK
 arrinit05.xi: OK
 arrinit06.xi: OK
 assign01.xi: OK
 assign02.xi: OK
 assign03.xi: OK
 assign04.xi: OK
 assign05.xi: OK
 assign06.xi: OK
 assign07.xi: OK
 assign08.xi: OK
 assign09.xi: OK
 assign10.xi: OK
 binary01.xi: OK
 binary02.xi: OK
 binary03.xi: OK
 binary04.xi: OK
 binary05.xi: OK
 binary06.xi: OK
 binary07.xi: OK
 binary08.xi: OK
 binary09.xi: OK
 binary10.xi: OK
 binary11.xi: OK
 binary12.xi: OK
 block01.xi: OK
 block02.xi: OK
 charlit01.xi: OK
 charlit02.xi: OK
 functioncall01.xi: OK
 functioncall02.xi: OK
 functioncall03.xi: OK
 functioncall04.xi: OK
 if01.xi: OK
 if02.xi: OK
 if03.xi: OK
 if04.xi: OK
 if05.xi: OK
 if06.xi: OK
 if07.xi: OK
 if08.xi: OK
 if09.xi: OK
 if10.xi: OK
 intlit01.xi: OK
 intlit02.xi: OK
 length01.xi: OK
 length02.xi: OK
 localdecl01.xi: OK
 localdecl02.xi: OK
 localdecl03.xi: OK
 localdecl04.xi: OK
 localdecl05.xi: OK
 localdecl06.xi: OK
 localdecl07.xi: OK
 localdecl08.xi: Syntax error for input symbol "EOF" spanning from 1:1 to 1:1
instead expected token classes are []
Couldn't repair and continue parse for input symbol "EOF" spanning from 1:1 to 1:1

IR syntax error: Can't recover from previous error(s)

---
# xic-ref (--irgen [basic test]): localdecl08.xi
Syntax error for input symbol "EOF" spanning from 1:1 to 1:1
instead expected token classes are []
Couldn't repair and continue parse for input symbol "EOF" spanning from 1:1 to 1:1

IR syntax error: Can't recover from previous error(s)
## Command line without filenames:
xic -libpath ../../../../../pa/pa4/grading/lib --irgen
## Content of test case:

```
use io
use conv

main(args: int[][]) {
  _ = f()
  println("done")
}

f(): int {
  println("f() called")
  return 17
}

```

## Compiler's standard error:
java.lang.ClassCastException: class ast.TypeDeclUnderscore cannot be cast to class ast.TypeDeclVar (ast.TypeDeclUnderscore and ast.TypeDeclVar are in unnamed module of loader 'app')
	at ast.VisitorTranslation.visit(VisitorTranslation.java:690)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:45)
	at ast.VisitorTranslation.lambda$visit$3(VisitorTranslation.java:786)
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1654)
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484)
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
	at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:913)
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:578)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:787)
	at ast.StmtBlock.accept(StmtBlock.java:54)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:821)
	at ast.FuncDefn.accept(FuncDefn.java:108)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:795)
	at ast.FileProgram.accept(FileProgram.java:67)
	at cli.CLI.buildIR(CLI.java:284)
	at cli.CLI.IRGen(CLI.java:215)
	at cli.CLI.run(CLI.java:105)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:316)

## Generated result for --irrun:

```

```

## Expected result for --irrun:

```
f() called
done

```

## Generated result for --irgen:

```

```

---
 localdecl09.xi: OK
 localdecl10.xi: Syntax error for input symbol "EOF" spanning from 1:1 to 1:1
instead expected token classes are []
Couldn't repair and continue parse for input symbol "EOF" spanning from 1:1 to 1:1

IR syntax error: Can't recover from previous error(s)

---
# xic-ref (--irgen [basic test]): localdecl10.xi
Syntax error for input symbol "EOF" spanning from 1:1 to 1:1
instead expected token classes are []
Couldn't repair and continue parse for input symbol "EOF" spanning from 1:1 to 1:1

IR syntax error: Can't recover from previous error(s)
## Command line without filenames:
xic -libpath ../../../../../pa/pa4/grading/lib --irgen
## Content of test case:

```
use io
use conv

main(args: int[][]) {
  x:int, _, z: int = f()
  println(unparseInt(x))
  println(unparseInt(z))
  println("done")
}

f(): int, bool, int {
  println("f() called")
  return 17, true, 42
}

```

## Compiler's standard error:
java.lang.ClassCastException: class ast.TypeDeclUnderscore cannot be cast to class ast.TypeDeclVar (ast.TypeDeclUnderscore and ast.TypeDeclVar are in unnamed module of loader 'app')
	at ast.VisitorTranslation.visit(VisitorTranslation.java:690)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:45)
	at ast.VisitorTranslation.lambda$visit$3(VisitorTranslation.java:786)
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1654)
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484)
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
	at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:913)
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:578)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:787)
	at ast.StmtBlock.accept(StmtBlock.java:54)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:821)
	at ast.FuncDefn.accept(FuncDefn.java:108)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:795)
	at ast.FileProgram.accept(FileProgram.java:67)
	at cli.CLI.buildIR(CLI.java:284)
	at cli.CLI.IRGen(CLI.java:215)
	at cli.CLI.run(CLI.java:105)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:316)

## Generated result for --irrun:

```

```

## Expected result for --irrun:

```
f() called
17
42
done

```

## Generated result for --irgen:

```

```

---
 localdecl11.xi: Syntax error for input symbol "EOF" spanning from 1:1 to 1:1
instead expected token classes are []
Couldn't repair and continue parse for input symbol "EOF" spanning from 1:1 to 1:1

IR syntax error: Can't recover from previous error(s)

---
# xic-ref (--irgen [basic test]): localdecl11.xi
Syntax error for input symbol "EOF" spanning from 1:1 to 1:1
instead expected token classes are []
Couldn't repair and continue parse for input symbol "EOF" spanning from 1:1 to 1:1

IR syntax error: Can't recover from previous error(s)
## Command line without filenames:
xic -libpath ../../../../../pa/pa4/grading/lib --irgen
## Content of test case:

```
use io
use conv

main(args: int[][]) {
  _, y: bool, z: int = f()
  if (y) println("true") else println("false")
  println(unparseInt(z))
  println("done")
}

f(): int, bool, int {
  println("f() called")
  return 17, true, 42
}

```

## Compiler's standard error:
java.lang.ClassCastException: class ast.TypeDeclUnderscore cannot be cast to class ast.TypeDeclVar (ast.TypeDeclUnderscore and ast.TypeDeclVar are in unnamed module of loader 'app')
	at ast.VisitorTranslation.visit(VisitorTranslation.java:690)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:45)
	at ast.VisitorTranslation.lambda$visit$3(VisitorTranslation.java:786)
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1654)
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484)
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
	at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:913)
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:578)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:787)
	at ast.StmtBlock.accept(StmtBlock.java:54)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:821)
	at ast.FuncDefn.accept(FuncDefn.java:108)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:795)
	at ast.FileProgram.accept(FileProgram.java:67)
	at cli.CLI.buildIR(CLI.java:284)
	at cli.CLI.IRGen(CLI.java:215)
	at cli.CLI.run(CLI.java:105)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:316)

## Generated result for --irrun:

```

```

## Expected result for --irrun:

```
f() called
true
42
done

```

## Generated result for --irgen:

```

```

---
 localdecl12.xi: Syntax error for input symbol "EOF" spanning from 1:1 to 1:1
instead expected token classes are []
Couldn't repair and continue parse for input symbol "EOF" spanning from 1:1 to 1:1

IR syntax error: Can't recover from previous error(s)

---
# xic-ref (--irgen [basic test]): localdecl12.xi
Syntax error for input symbol "EOF" spanning from 1:1 to 1:1
instead expected token classes are []
Couldn't repair and continue parse for input symbol "EOF" spanning from 1:1 to 1:1

IR syntax error: Can't recover from previous error(s)
## Command line without filenames:
xic -libpath ../../../../../pa/pa4/grading/lib --irgen
## Content of test case:

```
use io
use conv

main(args: int[][]) {
  _, _, _ = f()
  println("done")
}

f(): int, bool, int {
  println("f() called")
  return 17, true, 42
}

```

## Compiler's standard error:
java.lang.ClassCastException: class ast.TypeDeclUnderscore cannot be cast to class ast.TypeDeclVar (ast.TypeDeclUnderscore and ast.TypeDeclVar are in unnamed module of loader 'app')
	at ast.VisitorTranslation.visit(VisitorTranslation.java:690)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:45)
	at ast.VisitorTranslation.lambda$visit$3(VisitorTranslation.java:786)
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1654)
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484)
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
	at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:913)
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:578)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:787)
	at ast.StmtBlock.accept(StmtBlock.java:54)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:821)
	at ast.FuncDefn.accept(FuncDefn.java:108)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:795)
	at ast.FileProgram.accept(FileProgram.java:67)
	at cli.CLI.buildIR(CLI.java:284)
	at cli.CLI.IRGen(CLI.java:215)
	at cli.CLI.run(CLI.java:105)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:316)

## Generated result for --irrun:

```

```

## Expected result for --irrun:

```
f() called
done

```

## Generated result for --irgen:

```

```

---
 localdecl13.xi: Mismatch detected at line 4 of file localdecl13.ir.nml
expected: 2
found   : Index: 0

---
# xic-ref (--irgen [basic test]): localdecl13.xi
Mismatch detected at line 4 of file localdecl13.ir.nml
expected: 2
found   : Index: 0
## Command line without filenames:
xic -libpath ../../../../../pa/pa4/grading/lib --irgen
## Content of test case:

```
use io
use conv

main(args: int[][]) {
  a: int[] = {1, 2, 3}
  x: int[f(a, 0)][f(a, 0)][f(a, 0)][][]
  println(unparseInt(length(x)))
  println(unparseInt(length(x[0])))
  println(unparseInt(length(x[0][0])))
  println(unparseInt(a[0]))
  println(unparseInt(a[1]))
  println(unparseInt(a[2]))
}

f(a: int[], i: int): int {
  print("Index: ")
  println(unparseInt(i))
  a[i] = a[i] + 1
  return a[i]
}

```

## Generated result for --irrun:

```
Index: 0
Index: 0
Index: 0
Index: 0
Index: 0
Index: 0
Index: 0
Index: 0
Index: 0
Index: 0
Index: 0
Index: 0
Index: 0
2
3
4
14
2
3

```

## Expected result for --irrun:

```
Index: 0
Index: 0
Index: 0
2
3
4
4
2
3

```

## Generated result for --irgen:

```
(COMPUNIT
 localdecl13
 (FUNC
  _Imain_paai
  (SEQ
   (MOVE (TEMP args) (TEMP _ARG0))
   (MOVE (TEMP _mir_t1) (CONST 3))
   (MOVE (TEMP _lir_t0) (ADD (CONST 8) (MUL (TEMP _mir_t1) (CONST 8))))
   (MOVE (TEMP _lir_t1) (CALL (NAME _xi_alloc) (TEMP _lir_t0)))
   (MOVE (TEMP _mir_t2) (TEMP _lir_t1))
   (MOVE (MEM (TEMP _mir_t2)) (TEMP _mir_t1))
   (MOVE (TEMP _mir_t0) (ADD (TEMP _mir_t2) (CONST 8)))
   (MOVE (MEM (TEMP _mir_t0)) (CONST 1))
   (MOVE (MEM (ADD (TEMP _mir_t0) (CONST 8))) (CONST 2))
   (MOVE (MEM (ADD (TEMP _mir_t0) (CONST 16))) (CONST 3))
   (MOVE (TEMP a) (TEMP _mir_t0))
   (MOVE (TEMP _lir_t2) (TEMP a))
   (MOVE (TEMP _lir_t3) (CONST 0))
   (MOVE (TEMP _lir_t4) (CALL (NAME _If_iaii) (TEMP _lir_t2) (TEMP _lir_t3)))
   (MOVE (TEMP _mir_t3) (TEMP _RET0))
   (MOVE (TEMP _mir_t4) (TEMP _mir_t3))
   (MOVE (TEMP _lir_t5) (ADD (CONST 8) (MUL (TEMP _mir_t4) (CONST 8))))
   (MOVE (TEMP _lir_t6) (CALL (NAME _xi_alloc) (TEMP _lir_t5)))
   (MOVE (TEMP _mir_t5) (TEMP _lir_t6))
   (MOVE (MEM (TEMP _mir_t5)) (TEMP _mir_t4))
   (MOVE (TEMP x) (ADD (TEMP _mir_t5) (CONST 8)))
   (MOVE (TEMP _mir_t6) (CONST 0))
   (LABEL _mir_l0)
   (CJUMP (LT (TEMP _mir_t6) (TEMP _mir_t3)) _mir_l1)
   (MOVE (TEMP _lir_t17) (MEM (SUB (TEMP x) (CONST 8))))
   (MOVE (TEMP _lir_t18) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t17)))
   (MOVE (TEMP _lir_t19) (TEMP _RET0))
   (MOVE (TEMP _lir_t20) (CALL (NAME _Iprintln_pai) (TEMP _lir_t19)))
   (MOVE (TEMP _mir_t15) (TEMP x))
   (MOVE (TEMP _mir_t16) (CONST 0))
   (CJUMP
    (OR (LT (TEMP _mir_t16) (CONST 0))
     (GEQ (TEMP _mir_t16) (MEM (SUB (TEMP _mir_t15) (CONST 8)))))
    _mir_l9)
   (LABEL _mir_l10)
   (MOVE (TEMP _lir_t22)
    (MEM (ADD (TEMP _mir_t15) (MUL (CONST 8) (TEMP _mir_t16)))))
   (MOVE (TEMP _lir_t23) (MEM (SUB (TEMP _lir_t22) (CONST 8))))
   (MOVE (TEMP _lir_t24) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t23)))
   (MOVE (TEMP _lir_t25) (TEMP _RET0))
   (MOVE (TEMP _lir_t26) (CALL (NAME _Iprintln_pai) (TEMP _lir_t25)))
   (MOVE (TEMP _mir_t17) (TEMP x))
   (MOVE (TEMP _mir_t18) (CONST 0))
   (CJUMP
    (OR (LT (TEMP _mir_t18) (CONST 0))
     (GEQ (TEMP _mir_t18) (MEM (SUB (TEMP _mir_t17) (CONST 8)))))
    _mir_l11)
   (LABEL _mir_l12)
   (MOVE (TEMP _mir_t19)
    (MEM (ADD (TEMP _mir_t17) (MUL (CONST 8) (TEMP _mir_t18)))))
   (MOVE (TEMP _mir_t20) (CONST 0))
   (CJUMP
    (OR (LT (TEMP _mir_t20) (CONST 0))
     (GEQ (TEMP _mir_t20) (MEM (SUB (TEMP _mir_t19) (CONST 8)))))
    _mir_l13)
   (LABEL _mir_l14)
   (MOVE (TEMP _lir_t29)
    (MEM (ADD (TEMP _mir_t19) (MUL (CONST 8) (TEMP _mir_t20)))))
   (MOVE (TEMP _lir_t30) (MEM (SUB (TEMP _lir_t29) (CONST 8))))
   (MOVE (TEMP _lir_t31) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t30)))
   (MOVE (TEMP _lir_t32) (TEMP _RET0))
   (MOVE (TEMP _lir_t33) (CALL (NAME _Iprintln_pai) (TEMP _lir_t32)))
   (MOVE (TEMP _mir_t21) (TEMP a))
   (MOVE (TEMP _mir_t22) (CONST 0))
   (CJUMP
    (OR (LT (TEMP _mir_t22) (CONST 0))
     (GEQ (TEMP _mir_t22) (MEM (SUB (TEMP _mir_t21) (CONST 8)))))
    _mir_l15)
   (LABEL _mir_l16)
   (MOVE (TEMP _lir_t35)
    (MEM (ADD (TEMP _mir_t21) (MUL (CONST 8) (TEMP _mir_t22)))))
   (MOVE (TEMP _lir_t36) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t35)))
   (MOVE (TEMP _lir_t37) (TEMP _RET0))
   (MOVE (TEMP _lir_t38) (CALL (NAME _Iprintln_pai) (TEMP _lir_t37)))
   (MOVE (TEMP _mir_t23) (TEMP a))
   (MOVE (TEMP _mir_t24) (CONST 1))
   (CJUMP
    (OR (LT (TEMP _mir_t24) (CONST 0))
     (GEQ (TEMP _mir_t24) (MEM (SUB (TEMP _mir_t23) (CONST 8)))))
    _mir_l17)
   (LABEL _mir_l18)
   (MOVE (TEMP _lir_t40)
    (MEM (ADD (TEMP _mir_t23) (MUL (CONST 8) (TEMP _mir_t24)))))
   (MOVE (TEMP _lir_t41) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t40)))
   (MOVE (TEMP _lir_t42) (TEMP _RET0))
   (MOVE (TEMP _lir_t43) (CALL (NAME _Iprintln_pai) (TEMP _lir_t42)))
   (MOVE (TEMP _mir_t25) (TEMP a))
   (MOVE (TEMP _mir_t26) (CONST 2))
   (CJUMP
    (OR (LT (TEMP _mir_t26) (CONST 0))
     (GEQ (TEMP _mir_t26) (MEM (SUB (TEMP _mir_t25) (CONST 8)))))
    _mir_l19)
   (LABEL _mir_l20)
   (MOVE (TEMP _lir_t45)
    (MEM (ADD (TEMP _mir_t25) (MUL (CONST 8) (TEMP _mir_t26)))))
   (MOVE (TEMP _lir_t46) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t45)))
   (MOVE (TEMP _lir_t47) (TEMP _RET0))
   (MOVE (TEMP _lir_t48) (CALL (NAME _Iprintln_pai) (TEMP _lir_t47)))
   (RETURN)
   (LABEL _mir_l1)
   (MOVE (TEMP _lir_t7) (TEMP a))
   (MOVE (TEMP _lir_t8) (CONST 0))
   (MOVE (TEMP _lir_t9) (CALL (NAME _If_iaii) (TEMP _lir_t7) (TEMP _lir_t8)))
   (MOVE (TEMP _mir_t7) (TEMP _RET0))
   (MOVE (TEMP _mir_t8) (TEMP _mir_t7))
   (MOVE (TEMP _lir_t10) (ADD (CONST 8) (MUL (TEMP _mir_t8) (CONST 8))))
   (MOVE (TEMP _lir_t11) (CALL (NAME _xi_alloc) (TEMP _lir_t10)))
   (MOVE (TEMP _mir_t9) (TEMP _lir_t11))
   (MOVE (MEM (TEMP _mir_t9)) (TEMP _mir_t8))
   (MOVE (MEM (ADD (TEMP x) (MUL (TEMP _mir_t6) (CONST 8))))
    (ADD (TEMP _mir_t9) (CONST 8)))
   (MOVE (TEMP _mir_t10) (CONST 0))
   (LABEL _mir_l3)
   (CJUMP (LT (TEMP _mir_t10) (TEMP _mir_t7)) _mir_l4)
   (MOVE (TEMP _mir_t6) (ADD (TEMP _mir_t6) (CONST 1)))
   (JUMP (NAME _mir_l0))
   (LABEL _mir_l4)
   (MOVE (TEMP _lir_t12) (TEMP a))
   (MOVE (TEMP _lir_t13) (CONST 0))
   (MOVE (TEMP _lir_t14) (CALL (NAME _If_iaii) (TEMP _lir_t12) (TEMP _lir_t13)))
   (MOVE (TEMP _mir_t11) (TEMP _RET0))
   (MOVE (TEMP _mir_t12) (TEMP _mir_t11))
   (MOVE (TEMP _lir_t15) (ADD (CONST 8) (MUL (TEMP _mir_t12) (CONST 8))))
   (MOVE (TEMP _lir_t16) (CALL (NAME _xi_alloc) (TEMP _lir_t15)))
   (MOVE (TEMP _mir_t13) (TEMP _lir_t16))
   (MOVE (MEM (TEMP _mir_t13)) (TEMP _mir_t12))
   (MOVE
    (MEM
     (ADD (MEM (ADD (TEMP x) (MUL (TEMP _mir_t6) (CONST 8))))
      (MUL (TEMP _mir_t10) (CONST 8))))
    (ADD (TEMP _mir_t13) (CONST 8)))
   (MOVE (TEMP _mir_t14) (CONST 0))
   (LABEL _mir_l6)
   (CJUMP (LT (TEMP _mir_t14) (TEMP _mir_t11)) _mir_l7)
   (MOVE (TEMP _mir_t10) (ADD (TEMP _mir_t10) (CONST 1)))
   (JUMP (NAME _mir_l3))
   (LABEL _mir_l7)
   (MOVE (TEMP _mir_t14) (ADD (TEMP _mir_t14) (CONST 1)))
   (JUMP (NAME _mir_l6))
   (LABEL _mir_l9)
   (MOVE (TEMP _lir_t21) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l10))
   (LABEL _mir_l11)
   (MOVE (TEMP _lir_t27) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l12))
   (LABEL _mir_l13)
   (MOVE (TEMP _lir_t28) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l14))
   (LABEL _mir_l15)
   (MOVE (TEMP _lir_t34) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l16))
   (LABEL _mir_l17)
   (MOVE (TEMP _lir_t39) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l18))
   (LABEL _mir_l19)
   (MOVE (TEMP _lir_t44) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l20))))
 (FUNC
  _If_iaii
  (SEQ
   (MOVE (TEMP a) (TEMP _ARG0))
   (MOVE (TEMP i) (TEMP _ARG1))
   (MOVE (TEMP _mir_t28) (CONST 7))
   (MOVE (TEMP _lir_t49) (ADD (CONST 8) (MUL (TEMP _mir_t28) (CONST 8))))
   (MOVE (TEMP _lir_t50) (CALL (NAME _xi_alloc) (TEMP _lir_t49)))
   (MOVE (TEMP _mir_t29) (TEMP _lir_t50))
   (MOVE (MEM (TEMP _mir_t29)) (TEMP _mir_t28))
   (MOVE (TEMP _mir_t27) (ADD (TEMP _mir_t29) (CONST 8)))
   (MOVE (MEM (TEMP _mir_t27)) (CONST 73))
   (MOVE (MEM (ADD (TEMP _mir_t27) (CONST 8))) (CONST 110))
   (MOVE (MEM (ADD (TEMP _mir_t27) (CONST 16))) (CONST 100))
   (MOVE (MEM (ADD (TEMP _mir_t27) (CONST 24))) (CONST 101))
   (MOVE (MEM (ADD (TEMP _mir_t27) (CONST 32))) (CONST 120))
   (MOVE (MEM (ADD (TEMP _mir_t27) (CONST 40))) (CONST 58))
   (MOVE (MEM (ADD (TEMP _mir_t27) (CONST 48))) (CONST 32))
   (MOVE (TEMP _lir_t51) (TEMP _mir_t27))
   (MOVE (TEMP _lir_t52) (CALL (NAME _Iprint_pai) (TEMP _lir_t51)))
   (MOVE (TEMP _lir_t53) (TEMP i))
   (MOVE (TEMP _lir_t54) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t53)))
   (MOVE (TEMP _lir_t55) (TEMP _RET0))
   (MOVE (TEMP _lir_t56) (CALL (NAME _Iprintln_pai) (TEMP _lir_t55)))
   (MOVE (TEMP _mir_t30) (TEMP a))
   (MOVE (TEMP _mir_t31) (TEMP i))
   (CJUMP
    (OR (LT (TEMP _mir_t31) (CONST 0))
     (GEQ (TEMP _mir_t31) (MEM (SUB (TEMP _mir_t30) (CONST 8)))))
    _mir_l21)
   (LABEL _mir_l22)
   (MOVE (TEMP _mir_t32) (TEMP a))
   (MOVE (TEMP _mir_t33) (TEMP i))
   (CJUMP
    (OR (LT (TEMP _mir_t33) (CONST 0))
     (GEQ (TEMP _mir_t33) (MEM (SUB (TEMP _mir_t32) (CONST 8)))))
    _mir_l23)
   (LABEL _mir_l24)
   (MOVE (TEMP _lir_t59)
    (MEM (ADD (TEMP _mir_t32) (MUL (CONST 8) (TEMP _mir_t33)))))
   (MOVE (MEM (ADD (TEMP _mir_t30) (MUL (CONST 8) (TEMP _mir_t31))))
    (ADD (TEMP _lir_t59) (CONST 1)))
   (MOVE (TEMP _mir_t34) (TEMP a))
   (MOVE (TEMP _mir_t35) (TEMP i))
   (CJUMP
    (OR (LT (TEMP _mir_t35) (CONST 0))
     (GEQ (TEMP _mir_t35) (MEM (SUB (TEMP _mir_t34) (CONST 8)))))
    _mir_l25)
   (LABEL _mir_l26)
   (RETURN (MEM (ADD (TEMP _mir_t34) (MUL (CONST 8) (TEMP _mir_t35)))))
   (LABEL _mir_l21)
   (MOVE (TEMP _lir_t57) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l22))
   (LABEL _mir_l23)
   (MOVE (TEMP _lir_t58) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l24))
   (LABEL _mir_l25)
   (MOVE (TEMP _lir_t60) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l26)))))

```

---
 localdecl14.xi: Mismatch detected at line 7 of file localdecl14.ir.nml
expected: 1
found   : Index: 1

---
# xic-ref (--irgen [basic test]): localdecl14.xi
Mismatch detected at line 7 of file localdecl14.ir.nml
expected: 1
found   : Index: 1
## Command line without filenames:
xic -libpath ../../../../../pa/pa4/grading/lib --irgen
## Content of test case:

```
use io
use conv

main(args: int[][]) {
  a: int[] = {1, 2, 3, 4}
  x: int[f(a,3)-f(a,2)][f(a,2)-f(a,1)][f(a,1)-f(a,0)][][]
  println(unparseInt(length(x)))
  println(unparseInt(length(x[0])))
  println(unparseInt(length(x[0][0])))
  println(unparseInt(a[0]))
  println(unparseInt(a[1]))
  println(unparseInt(a[2]))
  println(unparseInt(a[3]))
}

f(a: int[], i: int): int {
  print("Index: ")
  println(unparseInt(i))
  a[i] = a[i] + 1
  return a[i]
}

```

## Generated result for --irrun:

```
Index: 3
Index: 2
Index: 2
Index: 1
Index: 1
Index: 0
Index: 1
Index: 0
1
2
2
3
5
5
5

```

## Expected result for --irrun:

```
Index: 3
Index: 2
Index: 2
Index: 1
Index: 1
Index: 0
1
2
2
2
4
5
5

```

## Generated result for --irgen:

```
(COMPUNIT
 localdecl14
 (FUNC
  _Imain_paai
  (SEQ
   (MOVE (TEMP args) (TEMP _ARG0))
   (MOVE (TEMP _mir_t1) (CONST 4))
   (MOVE (TEMP _lir_t0) (ADD (CONST 8) (MUL (TEMP _mir_t1) (CONST 8))))
   (MOVE (TEMP _lir_t1) (CALL (NAME _xi_alloc) (TEMP _lir_t0)))
   (MOVE (TEMP _mir_t2) (TEMP _lir_t1))
   (MOVE (MEM (TEMP _mir_t2)) (TEMP _mir_t1))
   (MOVE (TEMP _mir_t0) (ADD (TEMP _mir_t2) (CONST 8)))
   (MOVE (MEM (TEMP _mir_t0)) (CONST 1))
   (MOVE (MEM (ADD (TEMP _mir_t0) (CONST 8))) (CONST 2))
   (MOVE (MEM (ADD (TEMP _mir_t0) (CONST 16))) (CONST 3))
   (MOVE (MEM (ADD (TEMP _mir_t0) (CONST 24))) (CONST 4))
   (MOVE (TEMP a) (TEMP _mir_t0))
   (MOVE (TEMP _lir_t2) (TEMP a))
   (MOVE (TEMP _lir_t3) (CONST 3))
   (MOVE (TEMP _lir_t4) (CALL (NAME _If_iaii) (TEMP _lir_t2) (TEMP _lir_t3)))
   (MOVE (TEMP _lir_t8) (TEMP _RET0))
   (MOVE (TEMP _lir_t5) (TEMP a))
   (MOVE (TEMP _lir_t6) (CONST 2))
   (MOVE (TEMP _lir_t7) (CALL (NAME _If_iaii) (TEMP _lir_t5) (TEMP _lir_t6)))
   (MOVE (TEMP _mir_t3) (SUB (TEMP _lir_t8) (TEMP _RET0)))
   (MOVE (TEMP _mir_t4) (TEMP _mir_t3))
   (MOVE (TEMP _lir_t9) (ADD (CONST 8) (MUL (TEMP _mir_t4) (CONST 8))))
   (MOVE (TEMP _lir_t10) (CALL (NAME _xi_alloc) (TEMP _lir_t9)))
   (MOVE (TEMP _mir_t5) (TEMP _lir_t10))
   (MOVE (MEM (TEMP _mir_t5)) (TEMP _mir_t4))
   (MOVE (TEMP x) (ADD (TEMP _mir_t5) (CONST 8)))
   (MOVE (TEMP _mir_t6) (CONST 0))
   (LABEL _mir_l0)
   (CJUMP (LT (TEMP _mir_t6) (TEMP _mir_t3)) _mir_l1)
   (MOVE (TEMP _lir_t29) (MEM (SUB (TEMP x) (CONST 8))))
   (MOVE (TEMP _lir_t30) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t29)))
   (MOVE (TEMP _lir_t31) (TEMP _RET0))
   (MOVE (TEMP _lir_t32) (CALL (NAME _Iprintln_pai) (TEMP _lir_t31)))
   (MOVE (TEMP _mir_t15) (TEMP x))
   (MOVE (TEMP _mir_t16) (CONST 0))
   (CJUMP
    (OR (LT (TEMP _mir_t16) (CONST 0))
     (GEQ (TEMP _mir_t16) (MEM (SUB (TEMP _mir_t15) (CONST 8)))))
    _mir_l9)
   (LABEL _mir_l10)
   (MOVE (TEMP _lir_t34)
    (MEM (ADD (TEMP _mir_t15) (MUL (CONST 8) (TEMP _mir_t16)))))
   (MOVE (TEMP _lir_t35) (MEM (SUB (TEMP _lir_t34) (CONST 8))))
   (MOVE (TEMP _lir_t36) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t35)))
   (MOVE (TEMP _lir_t37) (TEMP _RET0))
   (MOVE (TEMP _lir_t38) (CALL (NAME _Iprintln_pai) (TEMP _lir_t37)))
   (MOVE (TEMP _mir_t17) (TEMP x))
   (MOVE (TEMP _mir_t18) (CONST 0))
   (CJUMP
    (OR (LT (TEMP _mir_t18) (CONST 0))
     (GEQ (TEMP _mir_t18) (MEM (SUB (TEMP _mir_t17) (CONST 8)))))
    _mir_l11)
   (LABEL _mir_l12)
   (MOVE (TEMP _mir_t19)
    (MEM (ADD (TEMP _mir_t17) (MUL (CONST 8) (TEMP _mir_t18)))))
   (MOVE (TEMP _mir_t20) (CONST 0))
   (CJUMP
    (OR (LT (TEMP _mir_t20) (CONST 0))
     (GEQ (TEMP _mir_t20) (MEM (SUB (TEMP _mir_t19) (CONST 8)))))
    _mir_l13)
   (LABEL _mir_l14)
   (MOVE (TEMP _lir_t41)
    (MEM (ADD (TEMP _mir_t19) (MUL (CONST 8) (TEMP _mir_t20)))))
   (MOVE (TEMP _lir_t42) (MEM (SUB (TEMP _lir_t41) (CONST 8))))
   (MOVE (TEMP _lir_t43) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t42)))
   (MOVE (TEMP _lir_t44) (TEMP _RET0))
   (MOVE (TEMP _lir_t45) (CALL (NAME _Iprintln_pai) (TEMP _lir_t44)))
   (MOVE (TEMP _mir_t21) (TEMP a))
   (MOVE (TEMP _mir_t22) (CONST 0))
   (CJUMP
    (OR (LT (TEMP _mir_t22) (CONST 0))
     (GEQ (TEMP _mir_t22) (MEM (SUB (TEMP _mir_t21) (CONST 8)))))
    _mir_l15)
   (LABEL _mir_l16)
   (MOVE (TEMP _lir_t47)
    (MEM (ADD (TEMP _mir_t21) (MUL (CONST 8) (TEMP _mir_t22)))))
   (MOVE (TEMP _lir_t48) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t47)))
   (MOVE (TEMP _lir_t49) (TEMP _RET0))
   (MOVE (TEMP _lir_t50) (CALL (NAME _Iprintln_pai) (TEMP _lir_t49)))
   (MOVE (TEMP _mir_t23) (TEMP a))
   (MOVE (TEMP _mir_t24) (CONST 1))
   (CJUMP
    (OR (LT (TEMP _mir_t24) (CONST 0))
     (GEQ (TEMP _mir_t24) (MEM (SUB (TEMP _mir_t23) (CONST 8)))))
    _mir_l17)
   (LABEL _mir_l18)
   (MOVE (TEMP _lir_t52)
    (MEM (ADD (TEMP _mir_t23) (MUL (CONST 8) (TEMP _mir_t24)))))
   (MOVE (TEMP _lir_t53) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t52)))
   (MOVE (TEMP _lir_t54) (TEMP _RET0))
   (MOVE (TEMP _lir_t55) (CALL (NAME _Iprintln_pai) (TEMP _lir_t54)))
   (MOVE (TEMP _mir_t25) (TEMP a))
   (MOVE (TEMP _mir_t26) (CONST 2))
   (CJUMP
    (OR (LT (TEMP _mir_t26) (CONST 0))
     (GEQ (TEMP _mir_t26) (MEM (SUB (TEMP _mir_t25) (CONST 8)))))
    _mir_l19)
   (LABEL _mir_l20)
   (MOVE (TEMP _lir_t57)
    (MEM (ADD (TEMP _mir_t25) (MUL (CONST 8) (TEMP _mir_t26)))))
   (MOVE (TEMP _lir_t58) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t57)))
   (MOVE (TEMP _lir_t59) (TEMP _RET0))
   (MOVE (TEMP _lir_t60) (CALL (NAME _Iprintln_pai) (TEMP _lir_t59)))
   (MOVE (TEMP _mir_t27) (TEMP a))
   (MOVE (TEMP _mir_t28) (CONST 3))
   (CJUMP
    (OR (LT (TEMP _mir_t28) (CONST 0))
     (GEQ (TEMP _mir_t28) (MEM (SUB (TEMP _mir_t27) (CONST 8)))))
    _mir_l21)
   (LABEL _mir_l22)
   (MOVE (TEMP _lir_t62)
    (MEM (ADD (TEMP _mir_t27) (MUL (CONST 8) (TEMP _mir_t28)))))
   (MOVE (TEMP _lir_t63) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t62)))
   (MOVE (TEMP _lir_t64) (TEMP _RET0))
   (MOVE (TEMP _lir_t65) (CALL (NAME _Iprintln_pai) (TEMP _lir_t64)))
   (RETURN)
   (LABEL _mir_l1)
   (MOVE (TEMP _lir_t11) (TEMP a))
   (MOVE (TEMP _lir_t12) (CONST 2))
   (MOVE (TEMP _lir_t13) (CALL (NAME _If_iaii) (TEMP _lir_t11) (TEMP _lir_t12)))
   (MOVE (TEMP _lir_t17) (TEMP _RET0))
   (MOVE (TEMP _lir_t14) (TEMP a))
   (MOVE (TEMP _lir_t15) (CONST 1))
   (MOVE (TEMP _lir_t16) (CALL (NAME _If_iaii) (TEMP _lir_t14) (TEMP _lir_t15)))
   (MOVE (TEMP _mir_t7) (SUB (TEMP _lir_t17) (TEMP _RET0)))
   (MOVE (TEMP _mir_t8) (TEMP _mir_t7))
   (MOVE (TEMP _lir_t18) (ADD (CONST 8) (MUL (TEMP _mir_t8) (CONST 8))))
   (MOVE (TEMP _lir_t19) (CALL (NAME _xi_alloc) (TEMP _lir_t18)))
   (MOVE (TEMP _mir_t9) (TEMP _lir_t19))
   (MOVE (MEM (TEMP _mir_t9)) (TEMP _mir_t8))
   (MOVE (MEM (ADD (TEMP x) (MUL (TEMP _mir_t6) (CONST 8))))
    (ADD (TEMP _mir_t9) (CONST 8)))
   (MOVE (TEMP _mir_t10) (CONST 0))
   (LABEL _mir_l3)
   (CJUMP (LT (TEMP _mir_t10) (TEMP _mir_t7)) _mir_l4)
   (MOVE (TEMP _mir_t6) (ADD (TEMP _mir_t6) (CONST 1)))
   (JUMP (NAME _mir_l0))
   (LABEL _mir_l4)
   (MOVE (TEMP _lir_t20) (TEMP a))
   (MOVE (TEMP _lir_t21) (CONST 1))
   (MOVE (TEMP _lir_t22) (CALL (NAME _If_iaii) (TEMP _lir_t20) (TEMP _lir_t21)))
   (MOVE (TEMP _lir_t26) (TEMP _RET0))
   (MOVE (TEMP _lir_t23) (TEMP a))
   (MOVE (TEMP _lir_t24) (CONST 0))
   (MOVE (TEMP _lir_t25) (CALL (NAME _If_iaii) (TEMP _lir_t23) (TEMP _lir_t24)))
   (MOVE (TEMP _mir_t11) (SUB (TEMP _lir_t26) (TEMP _RET0)))
   (MOVE (TEMP _mir_t12) (TEMP _mir_t11))
   (MOVE (TEMP _lir_t27) (ADD (CONST 8) (MUL (TEMP _mir_t12) (CONST 8))))
   (MOVE (TEMP _lir_t28) (CALL (NAME _xi_alloc) (TEMP _lir_t27)))
   (MOVE (TEMP _mir_t13) (TEMP _lir_t28))
   (MOVE (MEM (TEMP _mir_t13)) (TEMP _mir_t12))
   (MOVE
    (MEM
     (ADD (MEM (ADD (TEMP x) (MUL (TEMP _mir_t6) (CONST 8))))
      (MUL (TEMP _mir_t10) (CONST 8))))
    (ADD (TEMP _mir_t13) (CONST 8)))
   (MOVE (TEMP _mir_t14) (CONST 0))
   (LABEL _mir_l6)
   (CJUMP (LT (TEMP _mir_t14) (TEMP _mir_t11)) _mir_l7)
   (MOVE (TEMP _mir_t10) (ADD (TEMP _mir_t10) (CONST 1)))
   (JUMP (NAME _mir_l3))
   (LABEL _mir_l7)
   (MOVE (TEMP _mir_t14) (ADD (TEMP _mir_t14) (CONST 1)))
   (JUMP (NAME _mir_l6))
   (LABEL _mir_l9)
   (MOVE (TEMP _lir_t33) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l10))
   (LABEL _mir_l11)
   (MOVE (TEMP _lir_t39) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l12))
   (LABEL _mir_l13)
   (MOVE (TEMP _lir_t40) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l14))
   (LABEL _mir_l15)
   (MOVE (TEMP _lir_t46) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l16))
   (LABEL _mir_l17)
   (MOVE (TEMP _lir_t51) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l18))
   (LABEL _mir_l19)
   (MOVE (TEMP _lir_t56) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l20))
   (LABEL _mir_l21)
   (MOVE (TEMP _lir_t61) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l22))))
 (FUNC
  _If_iaii
  (SEQ
   (MOVE (TEMP a) (TEMP _ARG0))
   (MOVE (TEMP i) (TEMP _ARG1))
   (MOVE (TEMP _mir_t30) (CONST 7))
   (MOVE (TEMP _lir_t66) (ADD (CONST 8) (MUL (TEMP _mir_t30) (CONST 8))))
   (MOVE (TEMP _lir_t67) (CALL (NAME _xi_alloc) (TEMP _lir_t66)))
   (MOVE (TEMP _mir_t31) (TEMP _lir_t67))
   (MOVE (MEM (TEMP _mir_t31)) (TEMP _mir_t30))
   (MOVE (TEMP _mir_t29) (ADD (TEMP _mir_t31) (CONST 8)))
   (MOVE (MEM (TEMP _mir_t29)) (CONST 73))
   (MOVE (MEM (ADD (TEMP _mir_t29) (CONST 8))) (CONST 110))
   (MOVE (MEM (ADD (TEMP _mir_t29) (CONST 16))) (CONST 100))
   (MOVE (MEM (ADD (TEMP _mir_t29) (CONST 24))) (CONST 101))
   (MOVE (MEM (ADD (TEMP _mir_t29) (CONST 32))) (CONST 120))
   (MOVE (MEM (ADD (TEMP _mir_t29) (CONST 40))) (CONST 58))
   (MOVE (MEM (ADD (TEMP _mir_t29) (CONST 48))) (CONST 32))
   (MOVE (TEMP _lir_t68) (TEMP _mir_t29))
   (MOVE (TEMP _lir_t69) (CALL (NAME _Iprint_pai) (TEMP _lir_t68)))
   (MOVE (TEMP _lir_t70) (TEMP i))
   (MOVE (TEMP _lir_t71) (CALL (NAME _IunparseInt_aii) (TEMP _lir_t70)))
   (MOVE (TEMP _lir_t72) (TEMP _RET0))
   (MOVE (TEMP _lir_t73) (CALL (NAME _Iprintln_pai) (TEMP _lir_t72)))
   (MOVE (TEMP _mir_t32) (TEMP a))
   (MOVE (TEMP _mir_t33) (TEMP i))
   (CJUMP
    (OR (LT (TEMP _mir_t33) (CONST 0))
     (GEQ (TEMP _mir_t33) (MEM (SUB (TEMP _mir_t32) (CONST 8)))))
    _mir_l23)
   (LABEL _mir_l24)
   (MOVE (TEMP _mir_t34) (TEMP a))
   (MOVE (TEMP _mir_t35) (TEMP i))
   (CJUMP
    (OR (LT (TEMP _mir_t35) (CONST 0))
     (GEQ (TEMP _mir_t35) (MEM (SUB (TEMP _mir_t34) (CONST 8)))))
    _mir_l25)
   (LABEL _mir_l26)
   (MOVE (TEMP _lir_t76)
    (MEM (ADD (TEMP _mir_t34) (MUL (CONST 8) (TEMP _mir_t35)))))
   (MOVE (MEM (ADD (TEMP _mir_t32) (MUL (CONST 8) (TEMP _mir_t33))))
    (ADD (TEMP _lir_t76) (CONST 1)))
   (MOVE (TEMP _mir_t36) (TEMP a))
   (MOVE (TEMP _mir_t37) (TEMP i))
   (CJUMP
    (OR (LT (TEMP _mir_t37) (CONST 0))
     (GEQ (TEMP _mir_t37) (MEM (SUB (TEMP _mir_t36) (CONST 8)))))
    _mir_l27)
   (LABEL _mir_l28)
   (RETURN (MEM (ADD (TEMP _mir_t36) (MUL (CONST 8) (TEMP _mir_t37)))))
   (LABEL _mir_l23)
   (MOVE (TEMP _lir_t74) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l24))
   (LABEL _mir_l25)
   (MOVE (TEMP _lir_t75) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l26))
   (LABEL _mir_l27)
   (MOVE (TEMP _lir_t77) (CALL (NAME _xi_out_of_bounds)))
   (JUMP (NAME _mir_l28)))))

```

---
 procedurecall01.xi: OK
 procedurecall02.xi: OK
 procedurecall03.xi: OK
 procedurecall04.xi: OK
 procedurecall05.xi: OK
 procedurecall06.xi: OK
 stringlit01.xi: OK
 stringlit02.xi: OK
 unary01.xi: OK
 unary02.xi: OK
 unary03.xi: OK
 unary04.xi: OK
 while01.xi: OK
 while02.xi: OK
 while03.xi: OK
 while04.xi: OK
 xic-ref (--irgen [basic test]): 82 out of 88 tests succeeded.
Number of IRs: 84
Number of canonical IRs: 84
Number of constant-folded IRs: 84
Number of correct IRs: 82
 Test collection: xic-ref (--irgen [combo test])
 fac01.xi: OK
 fac02.xi: OK
 fac03.xi: OK
 fib01.xi: OK
 fib02.xi: OK
 medley01.xi: Syntax error for input symbol "EOF" spanning from 1:1 to 1:1
instead expected token classes are []
Couldn't repair and continue parse for input symbol "EOF" spanning from 1:1 to 1:1

IR syntax error: Can't recover from previous error(s)

---
# xic-ref (--irgen [combo test]): medley01.xi
Syntax error for input symbol "EOF" spanning from 1:1 to 1:1
instead expected token classes are []
Couldn't repair and continue parse for input symbol "EOF" spanning from 1:1 to 1:1

IR syntax error: Can't recover from previous error(s)
## Command line without filenames:
xic -libpath ../../../../../pa/pa4/grading/lib --irgen
## Content of test case:

```
use io
use conv

main(args:int[][]) {
  println({{"hello"}, {"world"}}[1][0])
  _ = id(id(id(1)))

  foo()

  aa:int[][] = {{1,2,3},{4,5},{6}}
  ff(aa)[1] = aa[2][0]
  println(unparseInt(aa[0][1]))
  println(unparseInt(length(aa[2])))

  aa[0][2] = gg(aa)
  println(unparseInt(aa[0][2]))
}

ff(a:int[][]): int[] {
  a[2] = {47,48,49,50}
  return a[0]
}

gg(a:int[][]): int {
  a[0] = {0}
  return 59
}

foo() {
  a:int[] = {1,2,3}
  a[f(a)[1]] = a[g(a)]
  println(unparseInt(a[0]))
  println(unparseInt(a[1]))
  println(unparseInt(a[2]))
}

f(a:int[]):int[] {
  println("hello");
  return a;
}

g(a:int[]):int {
  println("world");
  a[1] = 6
  return 1;
}

id(x: int): int {
  return x;
}

```

## Compiler's standard error:
java.lang.ClassCastException: class ast.TypeDeclUnderscore cannot be cast to class ast.TypeDeclVar (ast.TypeDeclUnderscore and ast.TypeDeclVar are in unnamed module of loader 'app')
	at ast.VisitorTranslation.visit(VisitorTranslation.java:690)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:45)
	at ast.VisitorTranslation.lambda$visit$3(VisitorTranslation.java:786)
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1654)
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484)
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474)
	at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:913)
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:578)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:787)
	at ast.StmtBlock.accept(StmtBlock.java:54)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:821)
	at ast.FuncDefn.accept(FuncDefn.java:108)
	at ast.VisitorTranslation.visit(VisitorTranslation.java:795)
	at ast.FileProgram.accept(FileProgram.java:67)
	at cli.CLI.buildIR(CLI.java:284)
	at cli.CLI.IRGen(CLI.java:215)
	at cli.CLI.run(CLI.java:105)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:316)

## Generated result for --irrun:

```

```

## Expected result for --irrun:

```
world
hello
world
1
6
6
47
4
_xi_out_of_bounds called

```

## Generated result for --irgen:

```

```

---
 xic-ref (--irgen [combo test]): 5 out of 6 tests succeeded.
Number of IRs: 5
Number of canonical IRs: 5
Number of constant-folded IRs: 5
Number of correct IRs: 5
 xthScript+O: 94 out of 101 tests succeeded.
