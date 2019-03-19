# Summary
 Test script: xthScript
 xic-build: OK
 Test collection: xic (Test --help)
 []: OK
 xic (Test --help): 1 out of 1 tests succeeded.
 Test collection: xic (Test --typecheck)
 ex01.xi: OK
 ex02.xi: OK
 ex03.xi: OK
 ex04.xi: OK
 ex05.xi: OK
 ex06.xi: OK
 ex07.xi: OK
 ex08.xi: OK
 ex09.xi: OK
 ex10.xi: OK
 ex11.xi: OK
 ex12.xi: OK
 spec1-full.xi: OK
 spec1.xi: OK
 spec2.xi: OK
 spec3.xi: OK
 gcd.xi: OK
 ratadd-full.xi: OK
 ratadd.xi: OK
 ratadduse-full.xi: OK
 ratadduse.xi: OK
 insertionsort.xi: OK
 arrayinit.xi: OK
 arrayinit2-full.xi: OK
 arrayinit2.xi: OK
 mdarrays.xi: OK
 xic (Test --typecheck): 26 out of 26 tests succeeded.
 Test collection: xic-ref (--typecheck [basic test])
 arracc01.xi: OK
 arracc02.xi: OK
 arracc03.xi: OK
 arracc04.xi: OK
 arracc05.xi: OK
 arracc06.xi: OK
 assign01.xi: OK
 assign02.xi: OK
 assign03.xi: OK
 assign04.xi: OK
 block01.xi: OK
 block02.xi: OK
 block03.xi: OK
 call01.xi: OK
 call02.xi: OK
 call03.xi: OK
 call04.xi: OK
 if01.xi: OK
 if02.xi: OK
 if03.xi: OK
 if04.xi: OK
 length01.xi: OK
 length02.xi: Missing line in file length02.typed.nml: Valid Xi Program

---
# xic-ref (--typecheck [basic test]): length02.xi
Missing line in file length02.typed.nml: Valid Xi Program
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: int[] = {47}
  x01: int = length({{}})
  x02: int = length({{1}})
  x03: int = length({{true, false}})
  x04: int = length({""})
  x05: int = length({x})
  x06: int = length({{{""}}, {{{0}}}})
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.equals(TypeTTauArray.java:82)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:270)
	at ast.ExprArrayLiteral.accept(ExprArrayLiteral.java:57)
	at ast.ExprLength.accept(ExprLength.java:28)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 lit01.xi: OK
 lit02.xi: OK
 lit03.xi: OK
 lit04.xi: OK
 lit05.xi: OK
 lit06.xi: Missing line in file lit06.typed.nml: Valid Xi Program

---
# xic-ref (--typecheck [basic test]): lit06.xi
Missing line in file lit06.typed.nml: Valid Xi Program
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x01: int[] = {0}
  x02: bool[] = {true, false}
  x03: int[] = {'a'}
  x04: int[][] = {""}

  x01': int[][] = {{0}}
  x02': bool[][] = {{true}, {false}}
  x03': int[][] = {{'a'}}
  x04': int[][][] = {{""}}
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.equals(TypeTTauArray.java:82)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:270)
	at ast.ExprArrayLiteral.accept(ExprArrayLiteral.java:57)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 op01.xi: OK
 op02.xi: OK
 op03.xi: OK
 op04.xi: OK
 op05.xi: OK
 op06.xi: OK
 op07.xi: OK
 op08.xi: OK
 op09.xi: Missing line in file op09.typed.nml: Valid Xi Program

---
# xic-ref (--typecheck [basic test]): op09.xi
Missing line in file op09.typed.nml: Valid Xi Program
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x01: bool = {0} == {1}
  x02: bool = {0} != {1}
  x03: bool = {true} == {false}
  x04: bool = {true} != {false}
  x05: bool = "" == "hello"
  x06: bool = "" != "hello"
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:107)
	at ast.ExprBinop.accept(ExprBinop.java:65)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 op10.xi: Missing line in file op10.typed.nml: Valid Xi Program

---
# xic-ref (--typecheck [basic test]): op10.xi
Missing line in file op10.typed.nml: Valid Xi Program
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: int[] = {47}
  x01: bool = {0} == x
  x02: bool = {0} != x
  x03: bool = x == {1}
  x04: bool = x != {1}

  x05: bool = {{true}} == {{false}}
  x06: bool = {{true}} != {{false}}
  x07: bool = {""} == {"hello"}
  x08: bool = {""} != {"hello"}

  x09: bool = "" == x
  x10: bool = "" != x
  x11: bool = x == "hello"
  x12: bool = x != "hello"

  x13: bool = {{{0}}, {{1}}, {{2}}} == {{{3}}, {{4}}, {{5}}}
  x14: bool = {{{0}}, {{1}}, {{2}}} != {{{3}}, {{4}}, {{5}}}
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.equals(TypeTTauArray.java:82)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:270)
	at ast.ExprArrayLiteral.accept(ExprArrayLiteral.java:57)
	at ast.ExprBinop.accept(ExprBinop.java:63)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 op11.xi: Missing line in file op11.typed.nml: Valid Xi Program

---
# xic-ref (--typecheck [basic test]): op11.xi
Missing line in file op11.typed.nml: Valid Xi Program
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x01: int[] = {0} + {1, 2}
  x02: bool[] = {true} + {false}
  x03: int[] = "" + "hello"
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:82)
	at ast.ExprBinop.accept(ExprBinop.java:65)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 op12.xi: Missing line in file op12.typed.nml: Valid Xi Program

---
# xic-ref (--typecheck [basic test]): op12.xi
Missing line in file op12.typed.nml: Valid Xi Program
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x01: int[][] = {{0}} + {{1}}
  x02: bool[][] = {{true}} + {{false}}
  x03: int[][] = {""} + {"hello"}

  x04: int[][] = {{0}} + {"hello"}
  x05: int[] = {0} + "hello"

  x06: int[][][] = {{{0}} + {{1}}}
  x07: bool[][][] = {{{true}} + {{false}}}
  x08: int[][][] = {{""} + {"hello"}}

  x09: int[][][] = {{{0}} + {"hello"}}
  x10: int[][] = {{0} + "hello"}
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.equals(TypeTTauArray.java:82)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:270)
	at ast.ExprArrayLiteral.accept(ExprArrayLiteral.java:57)
	at ast.ExprBinop.accept(ExprBinop.java:63)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 return01.xi: OK
 return02.xi: OK
 var01.xi: OK
 var02.xi: Missing line in file var02.typed.nml: Valid Xi Program

---
# xic-ref (--typecheck [basic test]): var02.xi
Missing line in file var02.typed.nml: Valid Xi Program
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x01: int[] = {0}
  y01: int[] = x01
  x02: bool[] = {true, false}
  y02: bool[] = x02
  x03: int[] = {'a'}
  y03: int[] = x03
  x04: int[][] = {""}
  y04: int[][] = x04

  x01': int[][] = {{0}}
  y01': int[][] = x01'
  x02': bool[][] = {{true}, {false}}
  y02': bool[][] = x02'
  x03': int[][] = {{'a'}}
  y03': int[][] = x03'
  x04': int[][][] = {{""}}
  y04': int[][][] = x04'
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.equals(TypeTTauArray.java:82)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:270)
	at ast.ExprArrayLiteral.accept(ExprArrayLiteral.java:57)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 var03.xi: OK
 var04.xi: OK
 var05.xi: Mismatch detected at line 1 of file var05.typed.nml
expected: Valid Xi Program
found   : : error:

---
# xic-ref (--typecheck [basic test]): var05.xi
Mismatch detected at line 1 of file var05.typed.nml
expected: Valid Xi Program
found   : : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
foo() {
  i00: int = f1()
  _ = f1()

  i01: int, b01: bool = f2();
  i02: int, _ = f2();
  _, b03: bool = f2();
  _, _ = f2();

  i05: int, b05: bool, ai05: int[] = f3();
  i06: int, b06: bool, _ = f3();
  i07: int, _, ai07: int[] = f3();
  i08: int, _, _ = f3();
  _, b09: bool, ai09: int[] = f3();
  _, b10: bool, _ = f3();
  _, _, ai11: int[] = f3();
  _, _, _ = f3();
}

f1(): int {
  return 0
}

f2(): int, bool {
  return 0, true
}

f3(): int, bool, int[] {
  return 0, true, {1}
}

```

## Compiler's standard output:
Semantic error beginning at ./var05.xi:3:3 error:Expected function call

## Generated result for --typecheck:

```
3:3 error:Expected function call

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 var06.xi: OK
 while01.xi: OK
 xic-ref (--typecheck [basic test]): 42 out of 50 tests succeeded.
 Test collection: xic-ref (--typecheck [use test])
 use01.xi: OK
 use02.xi: Mismatch detected at line 1 of file use02.typed.nml
expected: Valid Xi Program
found   : : error:

---
# xic-ref (--typecheck [use test]): use02.xi
Mismatch detected at line 1 of file use02.typed.nml
expected: Valid Xi Program
found   : : error:
## Command line without filenames:
xic -libpath ../../../../pa/pa3/grading/tests/use --typecheck
## Content of test case:

```
use i01
use i01

foo() {
}

```

## Compiler's standard output:
Semantic error beginning at ./use02.xi:1:1 error:Existing function with name p1 has different signature

## Generated result for --typecheck:

```
1:1 error:Existing function with name p1 has different signature

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 use03.xi: OK
 use04.xi: Mismatch detected at line 1 of file use04.typed.nml
expected: Valid Xi Program
found   : : error:

---
# xic-ref (--typecheck [use test]): use04.xi
Mismatch detected at line 1 of file use04.typed.nml
expected: Valid Xi Program
found   : : error:
## Command line without filenames:
xic -libpath ../../../../pa/pa3/grading/tests/use --typecheck
## Content of test case:

```
use i01
use i03

foo() {
  p1()
  p2(true)
  p3(0, {1})
  x01: int = f1()
  x02: int = f2(true)
  x03: int = f3(0, {1})
  x04: int, b04: bool = f4()
  x05: int, b05: bool = f5(true)
  x06: int, b06: bool = f6(0, {1})
  x07: bool = g1()
  x08: bool = g2(0)
}

```

## Compiler's standard output:
Semantic error beginning at ./use04.xi:1:1 error:Existing function with name p1 has different signature

## Generated result for --typecheck:

```
1:1 error:Existing function with name p1 has different signature

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 use05.xi: Mismatch detected at line 1 of file use05.typed.nml
expected: Valid Xi Program
found   : : error:

---
# xic-ref (--typecheck [use test]): use05.xi
Mismatch detected at line 1 of file use05.typed.nml
expected: Valid Xi Program
found   : : error:
## Command line without filenames:
xic -libpath ../../../../pa/pa3/grading/tests/use --typecheck
## Content of test case:

```
use i01

p1() {
}

f1(): int {
  return 0
}

```

## Compiler's standard output:
Semantic error beginning at ./use05.xi:3:1 error:Existing function with name p1 has different signature

## Generated result for --typecheck:

```
3:1 error:Existing function with name p1 has different signature

```

## Expected result for --typecheck:

```
Valid Xi Program

```

---
 xic-ref (--typecheck [use test]): 2 out of 5 tests succeeded.
 Test collection: xic-ref (--typecheck [basic-syntax-error test])
 assign01.xi: OK
 assign02.xi: OK
 assign03.xi: OK
 assign04.xi: OK
 block01.xi: OK
 block02.xi: OK
 call01.xi: OK
 call02.xi: OK
 call03.xi: OK
 codedecl01.xi: OK
 codedecl02.xi: OK
 codedecl03.xi: OK
 codedecl04.xi: OK
 codedecl05.xi: OK
 codedecl06.xi: OK
 codedecl07.xi: OK
 codedecl08.xi: OK
 codedecl09.xi: OK
 codedecl10.xi: OK
 codedecl11.xi: OK
 codedecl12.xi: OK
 codedecl13.xi: OK
 codedecl14.xi: OK
 codedecl15.xi: OK
 codedecl16.xi: OK
 codedecl17.xi: OK
 empty.xi: OK
 expr01.xi: OK
 expr02.xi: OK
 if01.xi: OK
 if02.xi: OK
 if03.xi: OK
 length01.xi: OK
 length02.xi: OK
 length03.xi: OK
 paramdecl01.xi: OK
 paramdecl02.xi: OK
 paramdecl03.xi: OK
 paramdecl04.xi: OK
 use01.xi: OK
 use02.xi: OK
 vardecl01.xi: OK
 vardecl02.xi: OK
 vardecl03.xi: OK
 vardecl04.xi: OK
 vardecl05.xi: OK
 vardecl06.xi: OK
 vardecl07.xi: OK
 vardecl08.xi: OK
 xic-ref (--typecheck [basic-syntax-error test]): 49 out of 49 tests succeeded.
 Test collection: xic-ref (--typecheck [combo-syntax-error test])
 group_of_anonymous01_01.xi: OK
 group_of_anonymous01_02.xi: OK
 group_of_anonymous01_03.xi: OK
 group_of_anonymous01_04.xi: OK
 group_of_anonymous01_05.xi: OK
 group_of_anonymous01_06.xi: OK
 group_of_anonymous01_07.xi: OK
 group_of_anonymous01_08.xi: OK
 group_of_anonymous01_09.xi: OK
 group_of_anonymous01_10.xi: OK
 group_of_anonymous01_11.xi: OK
 group_of_anonymous01_12.xi: OK
 group_of_anonymous01_13.xi: OK
 group_of_anonymous01_14.xi: OK
 group_of_anonymous01_15.xi: OK
 group_of_anonymous01_16.xi: OK
 group_of_anonymous01_17.xi: OK
 group_of_anonymous01_18.xi: OK
 group_of_anonymous01_19.xi: OK
 group_of_anonymous02_01.xi: OK
 group_of_anonymous03_01.xi: OK
 group_of_anonymous03_02.xi: OK
 group_of_anonymous04_01.xi: OK
 group_of_anonymous05_01.xi: OK
 group_of_anonymous06_01.xi: OK
 group_of_anonymous06_02.xi: OK
 group_of_anonymous06_03.xi: OK
 group_of_anonymous06_04.xi: OK
 group_of_anonymous06_05.xi: OK
 group_of_anonymous06_06.xi: OK
 group_of_anonymous06_07.xi: OK
 group_of_anonymous07_01.xi: OK
 group_of_anonymous08_01.xi: OK
 group_of_anonymous08_02.xi: OK
 group_of_anonymous09_01.xi: OK
 group_of_anonymous10_01.xi: OK
 group_of_anonymous10_02.xi: OK
 group_of_anonymous10_03.xi: OK
 group_of_anonymous11_01.xi: OK
 group_of_anonymous11_02.xi: OK
 group_of_anonymous11_03.xi: OK
 group_of_anonymous11_04.xi: OK
 xic-ref (--typecheck [combo-syntax-error test]): 42 out of 42 tests succeeded.
 Test collection: xic-ref (--typecheck [extension-syntax-error test (might succeed)])
 group_of_anonymous01_01.xi: OK
 vardecl01.xi: OK
 xic-ref (--typecheck [extension-syntax-error test (might succeed)]): 2 out of 2 tests succeeded.
 Test collection: xic-ref (--typecheck [basic-expr-error test])
 arracc01.xi: OK
 arracc02.xi: OK
 arracc03.xi: OK
 arracc04.xi: OK
 arracc05.xi: OK
 arracc06.xi: Missing line in file arracc06.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): arracc06.xi
Missing line in file arracc06.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  ai1: int[] = {0,1,2}
  x: int = ai1[""]
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at xic_error.SemanticTypeCheckError.<init>(SemanticTypeCheckError.java:13)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:235)
	at ast.ExprIndex.accept(ExprIndex.java:38)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
3:16 error:Expected int, but found int[]

```

---
 arracc07.xi: OK
 call01.xi: OK
 call02.xi: OK
 call03.xi: OK
 call04.xi: OK
 call05.xi: OK
 call06.xi: OK
 call07.xi: Missing line in file call07.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): call07.xi
Missing line in file call07.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
foo() {
  x: int = f(true)
}

f(x: int): int {
  return 0
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.VisitorTypeCheck.checkDeclaration(VisitorTypeCheck.java:442)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:499)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:39)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:14 error:Expected int, but found bool

```

---
 call08.xi: Missing line in file call08.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): call08.xi
Missing line in file call08.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
foo() {
  x: int = f()
}

f(x: int): int {
  return 0
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.VisitorTypeCheck.checkDeclaration(VisitorTypeCheck.java:442)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:499)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:39)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:12 error:Mismatched number of arguments: expected 1, but found 0

```

---
 call09.xi: OK
 call10.xi: OK
 call11.xi: OK
 call12.xi: OK
 length01.xi: OK
 length02.xi: OK
 length03.xi: OK
 length04.xi: OK
 length05.xi: OK
 length06.xi: OK
 length07.xi: OK
 length08.xi: OK
 length09.xi: OK
 length10.xi: OK
 lit01.xi: OK
 lit02.xi: OK
 lit03.xi: Missing line in file lit03.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): lit03.xi
Missing line in file lit03.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: int = ""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at xic_error.SemanticTypeCheckError.<init>(SemanticTypeCheckError.java:13)
	at ast.VisitorTypeCheck.checkDeclaration(VisitorTypeCheck.java:443)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:499)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:39)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:3 error:Expected int, but found int[]

```

---
 lit04.xi: OK
 lit05.xi: Missing line in file lit05.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): lit05.xi
Missing line in file lit05.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: bool = ""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at xic_error.SemanticTypeCheckError.<init>(SemanticTypeCheckError.java:13)
	at ast.VisitorTypeCheck.checkDeclaration(VisitorTypeCheck.java:443)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:499)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:39)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:3 error:Expected bool, but found int[]

```

---
 lit06.xi: OK
 lit07.xi: OK
 lit08.xi: OK
 lit09.xi: OK
 lit10.xi: OK
 lit11.xi: OK
 lit12.xi: OK
 op01.xi: OK
 op02.xi: OK
 op03.xi: OK
 op04.xi: OK
 op05.xi: OK
 op06.xi: OK
 op07.xi: OK
 op08.xi: OK
 op09.xi: OK
 op10.xi: OK
 op11.xi: OK
 op12.xi: OK
 op13.xi: Missing line in file op13.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): op13.xi
Missing line in file op13.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: int = 0 + ""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at ast.VisitorTypeCheck.throwSemanticErrorBinopVisit(VisitorTypeCheck.java:42)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:87)
	at ast.ExprBinop.accept(ExprBinop.java:65)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:12 error:Mismatched types: int and int[]

```

---
 op14.xi: Missing line in file op14.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): op14.xi
Missing line in file op14.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: int = "" + 1
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at ast.VisitorTypeCheck.throwSemanticErrorBinopVisit(VisitorTypeCheck.java:42)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:87)
	at ast.ExprBinop.accept(ExprBinop.java:65)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:12 error:Mismatched types: int[] and int

```

---
 op15.xi: OK
 op16.xi: OK
 op17.xi: OK
 op18.xi: OK
 op19.xi: Missing line in file op19.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): op19.xi
Missing line in file op19.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: int = -""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at xic_error.SemanticTypeCheckError.<init>(SemanticTypeCheckError.java:13)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:297)
	at ast.ExprUnop.accept(ExprUnop.java:45)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:12 error:Expected int, but found int[]

```

---
 op20.xi: OK
 op21.xi: OK
 op22.xi: OK
 op23.xi: OK
 op24.xi: OK
 op25.xi: OK
 op26.xi: OK
 op27.xi: OK
 op28.xi: OK
 op29.xi: OK
 op30.xi: OK
 op31.xi: OK
 op32.xi: OK
 op33.xi: Missing line in file op33.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): op33.xi
Missing line in file op33.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: bool = 0 == ""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at ast.VisitorTypeCheck.throwSemanticErrorBinopVisit(VisitorTypeCheck.java:42)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:112)
	at ast.ExprBinop.accept(ExprBinop.java:65)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:13 error:Mismatched types: int and int[]

```

---
 op34.xi: Missing line in file op34.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): op34.xi
Missing line in file op34.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: bool = "" == 1
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at ast.VisitorTypeCheck.throwSemanticErrorBinopVisit(VisitorTypeCheck.java:42)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:112)
	at ast.ExprBinop.accept(ExprBinop.java:65)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:13 error:Mismatched types: int[] and int

```

---
 op35.xi: OK
 op36.xi: OK
 op37.xi: OK
 op38.xi: OK
 op39.xi: Missing line in file op39.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): op39.xi
Missing line in file op39.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: int = !""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at xic_error.SemanticTypeCheckError.<init>(SemanticTypeCheckError.java:13)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:290)
	at ast.ExprUnop.accept(ExprUnop.java:45)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:12 error:Expected bool, but found int[]

```

---
 op40.xi: OK
 op41.xi: OK
 op42.xi: OK
 op43.xi: OK
 op44.xi: OK
 op45.xi: Missing line in file op45.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): op45.xi
Missing line in file op45.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: bool = true & ""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at ast.VisitorTypeCheck.throwSemanticErrorBinopVisit(VisitorTypeCheck.java:42)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:128)
	at ast.ExprBinop.accept(ExprBinop.java:65)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:13 error:Mismatched types: bool and int[]

```

---
 op46.xi: Missing line in file op46.typed.nml: : error:

---
# xic-ref (--typecheck [basic-expr-error test]): op46.xi
Missing line in file op46.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f() {
  x: bool = "" & false
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at ast.VisitorTypeCheck.throwSemanticErrorBinopVisit(VisitorTypeCheck.java:42)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:128)
	at ast.ExprBinop.accept(ExprBinop.java:65)
	at ast.StmtDeclAssign.accept(StmtDeclAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:13 error:Mismatched types: int[] and bool

```

---
 op47.xi: OK
 op48.xi: OK
 op49.xi: OK
 op50.xi: OK
 op51.xi: OK
 op52.xi: OK
 op53.xi: OK
 op54.xi: OK
 op55.xi: OK
 op56.xi: OK
 op57.xi: OK
 op58.xi: OK
 op59.xi: OK
 op60.xi: OK
 var01.xi: OK
 var02.xi: OK
 var03.xi: OK
 var04.xi: OK
 var05.xi: OK
 var06.xi: OK
 var07.xi: OK
 var08.xi: OK
 var09.xi: OK
 var10.xi: OK
 xic-ref (--typecheck [basic-expr-error test]): 98 out of 111 tests succeeded.
 Test collection: xic-ref (--typecheck [basic-stmt-error test])
 assign01.xi: OK
 assign02.xi: Missing line in file assign02.typed.nml: : error:

---
# xic-ref (--typecheck [basic-stmt-error test]): assign02.xi
Missing line in file assign02.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
foo() {
  x: int
  x = ""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at xic_error.SemanticTypeCheckError.<init>(SemanticTypeCheckError.java:13)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:410)
	at ast.StmtAssign.accept(StmtAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
3:3 error:Cannot assign int[] to int

```

---
 assign03.xi: OK
 assign04.xi: OK
 assign05.xi: OK
 assign06.xi: OK
 assign07.xi: OK
 assign08.xi: OK
 assign09.xi: OK
 assign10.xi: OK
 assign11.xi: OK
 assign12.xi: OK
 assign13.xi: OK
 assign14.xi: OK
 assign15.xi: OK
 assign16.xi: OK
 assign17.xi: OK
 assign18.xi: OK
 assign19.xi: OK
 assign20.xi: OK
 assign21.xi: OK
 assign22.xi: OK
 assign23.xi: OK
 assign24.xi: OK
 assign25.xi: Missing line in file assign25.typed.nml: : error:

---
# xic-ref (--typecheck [basic-stmt-error test]): assign25.xi
Missing line in file assign25.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
foo() {
  x: bool
  x = ""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at xic_error.SemanticTypeCheckError.<init>(SemanticTypeCheckError.java:13)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:410)
	at ast.StmtAssign.accept(StmtAssign.java:38)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
3:3 error:Cannot assign int[] to bool

```

---
 assign26.xi: OK
 assign27.xi: OK
 assign28.xi: OK
 assign29.xi: OK
 assign30.xi: OK
 assign31.xi: OK
 assign32.xi: OK
 assign33.xi: OK
 assign34.xi: OK
 assign35.xi: OK
 assign36.xi: OK
 assign37.xi: OK
 assign38.xi: OK
 assign39.xi: OK
 assign40.xi: OK
 assign41.xi: OK
 assign42.xi: OK
 assign43.xi: OK
 assign44.xi: OK
 assign45.xi: OK
 assign46.xi: OK
 assign47.xi: OK
 assign48.xi: OK
 assign49.xi: OK
 assign50.xi: OK
 assign51.xi: OK
 assign52.xi: OK
 assign53.xi: OK
 assign54.xi: OK
 assign55.xi: OK
 assign56.xi: OK
 assign57.xi: OK
 assign58.xi: OK
 assign59.xi: OK
 assign60.xi: OK
 assign61.xi: OK
 assign62.xi: OK
 assign63.xi: OK
 assign64.xi: OK
 assign65.xi: OK
 assign66.xi: OK
 assign67.xi: OK
 assign68.xi: OK
 assign69.xi: OK
 assign70.xi: OK
 assign71.xi: OK
 assign72.xi: OK
 assign73.xi: OK
 assign74.xi: OK
 assign75.xi: OK
 block01.xi: OK
 block02.xi: OK
 block03.xi: OK
 call01.xi: OK
 call02.xi: OK
 call03.xi: OK
 call04.xi: OK
 call05.xi: OK
 call06.xi: OK
 call07.xi: OK
 call08.xi: Missing line in file call08.typed.nml: : error:

---
# xic-ref (--typecheck [basic-stmt-error test]): call08.xi
Missing line in file call08.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
foo() {
  f()
}

f(x: int) {
}

```

## Compiler's standard error:
java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
	at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
	at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
	at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
	at java.base/java.util.Objects.checkIndex(Objects.java:372)
	at java.base/java.util.ArrayList.get(ArrayList.java:458)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:526)
	at ast.StmtProcedureCall.accept(StmtProcedureCall.java:40)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:3 error:Mismatched number of arguments: expected 1, but found 0

```

---
 call09.xi: OK
 call10.xi: OK
 call11.xi: OK
 if01.xi: OK
 if02.xi: OK
 if03.xi: OK
 if04.xi: OK
 if05.xi: OK
 return01.xi: OK
 return02.xi: OK
 return03.xi: OK
 return04.xi: OK
 return05.xi: OK
 return06.xi: Missing line in file return06.typed.nml: : error:

---
# xic-ref (--typecheck [basic-stmt-error test]): return06.xi
Missing line in file return06.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f(): int {
  return ""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at xic_error.SemanticTypeCheckError.<init>(SemanticTypeCheckError.java:13)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:333)
	at ast.StmtReturn.accept(StmtReturn.java:41)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:10 error:Expected int, but found int[]

```

---
 return07.xi: OK
 return08.xi: OK
 return09.xi: Missing line in file return09.typed.nml: : error:

---
# xic-ref (--typecheck [basic-stmt-error test]): return09.xi
Missing line in file return09.typed.nml: : error:
## Command line without filenames:
xic --typecheck
## Content of test case:

```
f(): bool {
  return ""
}

```

## Compiler's standard error:
java.lang.NullPointerException
	at ast.TypeTTauArray.toString(TypeTTauArray.java:25)
	at xic_error.SemanticTypeCheckError.<init>(SemanticTypeCheckError.java:13)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:333)
	at ast.StmtReturn.accept(StmtReturn.java:41)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:626)
	at ast.StmtBlock.accept(StmtBlock.java:44)
	at ast.VisitorTypeCheck.visit(VisitorTypeCheck.java:723)
	at ast.FuncDefn.accept(FuncDefn.java:102)
	at ast.FileProgram.accept(FileProgram.java:60)
	at cli.CLI.typeCheck(CLI.java:172)
	at cli.CLI.run(CLI.java:82)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:199)

## Generated result for --typecheck:

```

```

## Expected result for --typecheck:

```
2:10 error:Expected bool, but found int[]

```

---
 return10.xi: OK
 return11.xi: OK
 return12.xi: OK
 return13.xi: OK
 return14.xi: OK
 return15.xi: OK
 return16.xi: OK
 return17.xi: OK
 return18.xi: OK
 var01.xi: OK
 var02.xi: OK
 var03.xi: OK
 var04.xi: OK
 var05.xi: OK
 var06.xi: OK
 var07.xi: OK
 var08.xi: Mismatch detected at line 1 of file var08.typed.nml
expected: : error:
found   : Valid Xi Program

---
# xic-ref (--typecheck [basic-stmt-error test]): var08.xi
Mismatch detected at line 1 of file var08.typed.nml
expected: : error:
found   : Valid Xi Program
## Command line without filenames:
xic --typecheck
## Content of test case:

```
foo() {
  x: int[true]
}

```

## Generated result for --typecheck:

```
Valid Xi Program

```

## Expected result for --typecheck:

```
2:10 error:Expected int, but found bool

```

---
 var09.xi: OK
 var10.xi: OK
 var11.xi: OK
 var12.xi: OK
 var13.xi: OK
 var14.xi: OK
 var15.xi: OK
 var16.xi: OK
 var17.xi: OK
 var18.xi: OK
 while01.xi: OK
 while02.xi: OK
 xic-ref (--typecheck [basic-stmt-error test]): 126 out of 132 tests succeeded.
 Test collection: xic-ref (--typecheck [use-error test])
 decl01.xi: OK
 decl02.xi: OK
 decl03.xi: OK
 decl04.xi: OK
 decl05.xi: OK
 use01.xi: OK
 use02.xi: OK
 use03.xi: OK
 use04.xi: OK
 use05.xi: OK
 use06.xi: OK
 use07.xi: OK
 xic-ref (--typecheck [use-error test]): 12 out of 12 tests succeeded.
 xthScript: 401 out of 431 tests succeeded.
