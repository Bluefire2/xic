# Summary
 Test script: xthScript
 xic-build: OK
 Test collection: xic (Test --help)
 []: OK
 xic (Test --help): 1 out of 1 tests succeeded.
 Test collection: xic (Test --lex)
 ex1.xi: OK
 ex2.xi: OK
 spec1.xi: OK
 spec2.xi: OK
 spec3.xi: OK
 gcd.xi: OK
 ratadd.xi: OK
 ratadduse.xi: OK
 insertionsort.xi: OK
 arrayinit.xi: OK
 arrayinit2.xi: OK
 mdarrays.xi: OK
 add.xi: OK
 beauty.xi: OK
 xic (Test --lex): 14 out of 14 tests succeeded.
 Test collection: xic-ref (--lex [basic test])
 char01.xi: OK
 char02.xi: OK
 char03.xi: Mismatch detected at line 1 of file char03.lexed.nml
expected: 1:1 character \"
found   : 1:2 error:

---
# xic-ref (--lex [basic test]): char03.xi
Mismatch detected at line 1 of file char03.lexed.nml
expected: 1:1 character \"
found   : 1:2 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
'\"'

```

---
 comment01.xi: OK
 comment02.xi: OK
 comment03.xi: OK
 comment04.xi: OK
 comment05.xi: OK
 id01.xi: OK
 id02.xi: OK
 id03.xi: OK
 id04.xi: OK
 id05.xi: OK
 id06.xi: OK
 ids01.xi: OK
 int01.xi: OK
 int02.xi: OK
 int03.xi: OK
 int04.xi: OK
 int05.xi: OK
 int06.xi: Mismatch detected at line 1 of file int06.lexed.nml
expected: 1:1 -
found   : 1:1 integer -9223372036854775808

---
# xic-ref (--lex [basic test]): int06.xi
Mismatch detected at line 1 of file int06.lexed.nml
expected: 1:1 -
found   : 1:1 integer -9223372036854775808
## Command line without filenames:
xic --lex
## Content of test case:

```
-9223372036854775808 //Min int

```

---
 int07.xi: OK
 keyword01.xi: OK
 keyword02.xi: OK
 keyword03.xi: OK
 keyword04.xi: OK
 keyword05.xi: OK
 keyword06.xi: OK
 keyword07.xi: OK
 keyword08.xi: OK
 keyword09.xi: Mismatch detected at line 1 of file keyword09.lexed.nml
expected: 1:1 false
found   : 1:1 bool false

---
# xic-ref (--lex [basic test]): keyword09.xi
Mismatch detected at line 1 of file keyword09.lexed.nml
expected: 1:1 false
found   : 1:1 bool false
## Command line without filenames:
xic --lex
## Content of test case:

```
false

```

---
 keyword10.xi: Mismatch detected at line 1 of file keyword10.lexed.nml
expected: 1:1 true
found   : 1:1 bool true

---
# xic-ref (--lex [basic test]): keyword10.xi
Mismatch detected at line 1 of file keyword10.lexed.nml
expected: 1:1 true
found   : 1:1 bool true
## Command line without filenames:
xic --lex
## Content of test case:

```
true

```

---
 string01.xi: OK
 string02.xi: OK
 string03.xi: OK
 string04.xi: OK
 string05.xi: OK
 string06.xi: OK
 string07.xi: OK
 string08.xi: Missing line in file string08.lexed.nml: 1:1 string \\

---
# xic-ref (--lex [basic test]): string08.xi
Missing line in file string08.lexed.nml: 1:1 string \\
## Command line without filenames:
xic --lex
## Content of test case:

```
"\\"

```

---
 string09.xi: OK
 string10.xi: OK
 string11.xi: OK
 string12.xi: OK
 sym01.xi: OK
 sym02.xi: OK
 sym03.xi: OK
 sym04.xi: OK
 sym05.xi: OK
 sym06.xi: OK
 sym07.xi: OK
 sym08.xi: OK
 sym09.xi: OK
 sym10.xi: OK
 sym11.xi: OK
 sym12.xi: OK
 sym13.xi: OK
 sym14.xi: OK
 sym15.xi: OK
 sym16.xi: OK
 sym17.xi: OK
 sym18.xi: OK
 sym19.xi: OK
 sym20.xi: OK
 sym21.xi: OK
 sym22.xi: OK
 sym23.xi: OK
 sym24.xi: OK
 sym25.xi: OK
 sym26.xi: OK
 symbols.xi: OK
 xic-ref (--lex [basic test]): 66 out of 71 tests succeeded.
 Test collection: xic-ref (--lex [basic-error test])
 char01.xi: OK
 char02.xi: Mismatch detected at line 1 of file char02.lexed.nml
expected: 1:1 error:
found   : 1:2 error:

---
# xic-ref (--lex [basic-error test]): char02.xi
Mismatch detected at line 1 of file char02.lexed.nml
expected: 1:1 error:
found   : 1:2 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
'
'

```

---
 char03.xi: Mismatch detected at line 1 of file char03.lexed.nml
expected: 1:1 error:
found   : 1:2 error:

---
# xic-ref (--lex [basic-error test]): char03.xi
Mismatch detected at line 1 of file char03.lexed.nml
expected: 1:1 error:
found   : 1:2 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
'

```

---
 char04.xi: Mismatch detected at line 1 of file char04.lexed.nml
expected: 1:1 error:
found   : 1:2 error:

---
# xic-ref (--lex [basic-error test]): char04.xi
Mismatch detected at line 1 of file char04.lexed.nml
expected: 1:1 error:
found   : 1:2 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
'asd'

```

---
 char05.xi: OK
 char06.xi: Mismatch detected at line 1 of file char06.lexed.nml
expected: 1:1 error:
found   : 1:2 error:

---
# xic-ref (--lex [basic-error test]): char06.xi
Mismatch detected at line 1 of file char06.lexed.nml
expected: 1:1 error:
found   : 1:2 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
'a

```

---
 char07.xi: Mismatch detected at line 1 of file char07.lexed.nml
expected: 1:1 error:
found   : 1:2 error:

---
# xic-ref (--lex [basic-error test]): char07.xi
Mismatch detected at line 1 of file char07.lexed.nml
expected: 1:1 error:
found   : 1:2 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
'asdf

```

---
 char08.xi: Mismatch detected at line 1 of file char08.lexed.nml
expected: 1:1 error:
found   : 1:2 error:

---
# xic-ref (--lex [basic-error test]): char08.xi
Mismatch detected at line 1 of file char08.lexed.nml
expected: 1:1 error:
found   : 1:2 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
'asdf

```

---
 char09.xi: Mismatch detected at line 1 of file char09.lexed.nml
expected: 1:1 error:
found   : 1:2 error:

---
# xic-ref (--lex [basic-error test]): char09.xi
Mismatch detected at line 1 of file char09.lexed.nml
expected: 1:1 error:
found   : 1:2 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
'a

```

---
 string01.xi: Mismatch detected at line 1 of file string01.lexed.nml
expected: 1:1 error:
found   : 1:4 error:

---
# xic-ref (--lex [basic-error test]): string01.xi
Mismatch detected at line 1 of file string01.lexed.nml
expected: 1:1 error:
found   : 1:4 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
"\"

```

---
 string02.xi: Mismatch detected at line 1 of file string02.lexed.nml
expected: 1:1 error:
found   : 1:2 error:

---
# xic-ref (--lex [basic-error test]): string02.xi
Mismatch detected at line 1 of file string02.lexed.nml
expected: 1:1 error:
found   : 1:2 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
"

```

---
 string03.xi: Mismatch detected at line 1 of file string03.lexed.nml
expected: 1:2 error:
found   : 1:1 string \q

---
# xic-ref (--lex [basic-error test]): string03.xi
Mismatch detected at line 1 of file string03.lexed.nml
expected: 1:2 error:
found   : 1:1 string \q
## Command line without filenames:
xic --lex
## Content of test case:

```
"\q"

```

---
 string04.xi: Mismatch detected at line 1 of file string04.lexed.nml
expected: 1:2 error:
found   : 1:1 string \x

---
# xic-ref (--lex [basic-error test]): string04.xi
Mismatch detected at line 1 of file string04.lexed.nml
expected: 1:2 error:
found   : 1:1 string \x
## Command line without filenames:
xic --lex
## Content of test case:

```
"\x"

```

---
 string05.xi: Mismatch detected at line 1 of file string05.lexed.nml
expected: 1:1 error:
found   : 1:6 error:

---
# xic-ref (--lex [basic-error test]): string05.xi
Mismatch detected at line 1 of file string05.lexed.nml
expected: 1:1 error:
found   : 1:6 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
"\\\"

```

---
 string06.xi: Mismatch detected at line 1 of file string06.lexed.nml
expected: 1:2 error:
found   : 1:1 string \ \ \ 

---
# xic-ref (--lex [basic-error test]): string06.xi
Mismatch detected at line 1 of file string06.lexed.nml
expected: 1:2 error:
found   : 1:1 string \ \ \ 
## Command line without filenames:
xic --lex
## Content of test case:

```
"\ \ \ "

```

---
 string07.xi: Missing line in file string07.lexed.nml: 1:41 error:

---
# xic-ref (--lex [basic-error test]): string07.xi
Missing line in file string07.lexed.nml: 1:41 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
"""""""""""""""""""""""""""""""""""""""""

```

---
 string08.xi: Mismatch detected at line 1 of file string08.lexed.nml
expected: 1:1 error:
found   : 1:9 error:

---
# xic-ref (--lex [basic-error test]): string08.xi
Mismatch detected at line 1 of file string08.lexed.nml
expected: 1:1 error:
found   : 1:9 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
"abcdedf

```

---
 string09.xi: Missing line in file string09.lexed.nml: 1:1 error:

---
# xic-ref (--lex [basic-error test]): string09.xi
Missing line in file string09.lexed.nml: 1:1 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
"abcdedf

```

---
 xic-ref (--lex [basic-error test]): 2 out of 18 tests succeeded.
 Test collection: xic-ref (--lex [combo test])
 char01.xi: OK
 char02.xi: OK
 char03.xi: OK
 char04.xi: OK
 char05.xi: OK
 comments01.xi: OK
 int01.xi: OK
 keywords01.xi: OK
 medley01.xi: OK
 prog01.xi: OK
 string01.xi: OK
 string02.xi: OK
 string03.xi: OK
 string04.xi: OK
 string05.xi: Missing line in file string05.lexed.nml: 1:28 string \\

---
# xic-ref (--lex [combo test]): string05.xi
Missing line in file string05.lexed.nml: 1:28 string \\
## Command line without filenames:
xic --lex
## Content of test case:

```
"Hello ""\"World\"""; x = ""\\"

```

---
 sym01.xi: OK
 sym02.xi: OK
 sym03.xi: OK
 sym04.xi: OK
 sym05.xi: OK
 sym06.xi: OK
 xic-ref (--lex [combo test]): 20 out of 21 tests succeeded.
 Test collection: xic-ref (--lex [extension test (might fail)])
 int01.xi: OK
 xic-ref (--lex [extension test (might fail)]): 1 out of 1 tests succeeded.
 Test collection: xic-ref (--lex [extension-error test (might succeed)])
 char01.xi: OK
 string01.xi: Mismatch detected at line 1 of file string01.lexed.nml
expected: 1:1 error:
found   : 1:12 error:

---
# xic-ref (--lex [extension-error test (might succeed)]): string01.xi
Mismatch detected at line 1 of file string01.lexed.nml
expected: 1:1 error:
found   : 1:12 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
"This might
not work"

```

---
 string02.xi: Mismatch detected at line 1 of file string02.lexed.nml
expected: 1:1 error:
found   : 1:2 error:

---
# xic-ref (--lex [extension-error test (might succeed)]): string02.xi
Mismatch detected at line 1 of file string02.lexed.nml
expected: 1:1 error:
found   : 1:2 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
"
"

```

---
 sym01.xi: OK
 xic-ref (--lex [extension-error test (might succeed)]): 2 out of 4 tests succeeded.
 Test collection: xic-ref (Test --lex)
 lex01.xi: Mismatch detected at line 10 of file lex01.lexed.nml
expected: 1:18 true
found   : 1:18 bool true

---
# xic-ref (Test --lex): lex01.xi
Mismatch detected at line 10 of file lex01.lexed.nml
expected: 1:18 true
found   : 1:18 bool true
## Command line without filenames:
xic --lex
## Content of test case:

```
x:(bool, int) = (true, 1)

```

---
 lex02.xi: OK
 lex03.xi: OK
 lex04.xi: OK
 lex05.xi: OK
 lex06.xi: OK
 lex07.xi: Mismatch detected at line 42 of file lex07.lexed.nml
expected: 1:101 false
found   : 1:101 bool false

---
# xic-ref (Test --lex): lex07.xi
Mismatch detected at line 42 of file lex07.lexed.nml
expected: 1:101 false
found   : 1:101 bool false
## Command line without filenames:
xic --lex
## Content of test case:

```
use library; while ( a <= length b | (c+d) % 2 > 0) { f = e*g/h; if (!more & a ) break; else more = false;}

```

---
 lex08.xi: OK
 parse-error01.xi: OK
 parse-error02.xi: OK
 parse-error03.xi: OK
 parse-error04.xi: OK
 parse-error05.xi: Mismatch detected at line 6 of file parse-error05.lexed.nml
expected: 2:5 false
found   : 2:5 bool false

---
# xic-ref (Test --lex): parse-error05.xi
Mismatch detected at line 6 of file parse-error05.lexed.nml
expected: 2:5 false
found   : 2:5 bool false
## Command line without filenames:
xic --lex
## Content of test case:

```
use io;
if (false)
b:int=0;
else if (true)
x:int=1;


```

---
 parse-error06.xi: OK
 parse-error07.xi: OK
 parse-error08.xi: OK
 parse-error09.xi: OK
 parse-error10.xi: OK
 parse-error11.xi: OK
 parse-error12.xi: OK
 parse-error13.xi: OK
 parse-error14.xi: OK
 parse-error15.xi: OK
 parse-error16.xi: OK
 parse-error17.xi: OK
 parse-error18.xi: OK
 parse-error19.xi: OK
 parse-error20.xi: OK
 parse01.xi: Mismatch detected at line 24 of file parse01.lexed.nml
expected: 2:37 true
found   : 2:37 bool true

---
# xic-ref (Test --lex): parse01.xi
Mismatch detected at line 24 of file parse01.lexed.nml
expected: 2:37 true
found   : 2:37 bool true
## Command line without filenames:
xic --lex
## Content of test case:

```
use io;
a() = {  x:int = 2;  y:(bool, int)=(true, 1);  z:int;  (b:bool, i:int) = y;  s:int [ ] = "Hello";  (x:int, _, z:int) = (1,2,3);  a: int [] = (72, 101, 108, 108, 111);  a: int [] = "Hello";}

```

---
 parse02.xi: Mismatch detected at line 15 of file parse02.lexed.nml
expected: 3:6 false
found   : 3:6 bool false

---
# xic-ref (Test --lex): parse02.xi
Mismatch detected at line 15 of file parse02.lexed.nml
expected: 3:6 false
found   : 3:6 bool false
## Command line without filenames:
xic --lex
## Content of test case:

```
assign() = {
x=x+1;
y = (false,0);
b = ! b;
s = (1,2,3,4);
a n = n;}

```

---
 parse03.xi: Mismatch detected at line 13 of file parse03.lexed.nml
expected: 2:6 false
found   : 2:6 bool false

---
# xic-ref (Test --lex): parse03.xi
Mismatch detected at line 13 of file parse03.lexed.nml
expected: 2:6 false
found   : 2:6 bool false
## Command line without filenames:
xic --lex
## Content of test case:

```
p( ): int[] = {
b = !false | b | c& !d
!= true;x = -a%2+b-c*-3/-d >= 0;x = a b c;}

```

---
 parse04.xi: OK
 parse05.xi: OK
 parse06.xi: OK
 parse07.xi: OK
 parse08.xi: OK
 parse09.xi: OK
 parse10.xi: OK
 parse11.xi: OK
 unicode.xi: OK
 largeintliteral.xi: Missing line in file largeintliteral.lexed.nml: 2:14 error:

---
# xic-ref (Test --lex): largeintliteral.xi
Missing line in file largeintliteral.lexed.nml: 2:14 error:
## Command line without filenames:
xic --lex
## Content of test case:

```
main(args: int[][]) {
    b: int = 1000000000000000000000000000000;
}

```

## Compiler's standard error:
java.lang.NumberFormatException: For input string: "1000000000000000000000000000000"
	at java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
	at java.base/java.lang.Long.parseLong(Long.java:692)
	at java.base/java.lang.Long.<init>(Long.java:1317)
	at lexer.XiLexer.yylex(XiLexer.java:730)
	at cli.CLI.lex(CLI.java:58)
	at cli.CLI.run(CLI.java:40)
	at picocli.CommandLine.execute(CommandLine.java:1160)
	at picocli.CommandLine.access$800(CommandLine.java:141)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1367)
	at picocli.CommandLine$RunLast.handle(CommandLine.java:1335)
	at picocli.CommandLine$AbstractParseResultHandler.handleParseResult(CommandLine.java:1243)
	at picocli.CommandLine.parseWithHandlers(CommandLine.java:1526)
	at picocli.CommandLine.run(CommandLine.java:1974)
	at picocli.CommandLine.run(CommandLine.java:1904)
	at cli.CLI.main(CLI.java:74)

---
 xic-ref (Test --lex): 34 out of 41 tests succeeded.
 xthScript: 141 out of 172 tests succeeded.
