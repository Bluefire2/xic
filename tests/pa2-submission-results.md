# Summary
 Test script: xthScript
 xic-build: OK
 Test collection: xic (Test --help)
 []: OK
 xic (Test --help): 1 out of 1 tests succeeded.
 Test collection: xic (Test --parse)
 ex1.xi: OK
 ex2.xi: OK
 ex3.xi: OK
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
 io.ixi: OK
 xic (Test --parse): 16 out of 16 tests succeeded.
 Test collection: xic-ref (--parse [basic test])
 assign01.xi: Mismatch detected at line 8 of file assign01.parsed.nml
expected:   (g1 () (int) ((return 0)))))
found   :   (g1 () (int) ())))

---
# xic-ref (--parse [basic test]): assign01.xi
Mismatch detected at line 8 of file assign01.parsed.nml
expected:   (g1 () (int) ((return 0)))))
found   :   (g1 () (int) ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int, y:bool, z:int[]) {
  x = 0
  y = true
  x = x
  z = {1, 2, 3}
  x = g1()
  x = z[0]
  x = -1
  x = 0 + 0
  x = (0)
}

g1():int {
  return 0
}

```

## Generated result for --parse:

```
(()
 ((f
   ((x int) (y bool) (z ([] int)))
   ()
   ((= x 0)
    (= y true)
    (= x x)
    (= z (1 2 3))
    (= x (g1))
    (= x ([] z 0))
    (= x (- 1))
    (= x (+ 0 0))
    (= x 0)))
  (g1 () (int) ())))

```

## Expected result for --parse:

```
(()
 ((f
   ((x int) (y bool) (z ([] int)))
   ()
   ((= x 0)
    (= y true) (= x x) (= z (1 2 3)) (= x (g1)) (= x ([] z 0)) (= x (- 1))
    (= x (+ 0 0)) (= x 0)))
  (g1 () (int) ((return 0)))))

```

---
 assign02.xi: Mismatch detected at line 1 of file assign02.parsed.nml
expected: (()
found   : 4:7 error:

---
# xic-ref (--parse [basic test]): assign02.xi
Mismatch detected at line 1 of file assign02.parsed.nml
expected: (()
found   : 4:7 error:
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x1:int[], x2:int[][]) {
  x1[0] = 0
  x2[0][1] = 0
  g1()[0] = 0
  g2()[0][1] = 0
}

g1():int[] {
  return {0}
}

g2():int[][] {
  return {{0}}
}

```

## Compiler's standard output:
Syntax Error
4:7 error:Unexpected token [

## Generated result for --parse:

```
4:7 error:Unexpected token [

```

## Expected result for --parse:

```
(()
 ((f
   ((x1 ([] int)) (x2 ([] ([] int))))
   ()
   ((=
     ([] x1 0) 0)
    (= ([] ([] x2 0) 1) 0)
    (= ([] (g1) 0) 0)
    (= ([] ([] (g2) 0) 1) 0)))
  (g1 () (([] int)) ((return (0))))
  (g2 () (([] ([] int))) ((return ((0)))))))

```

---
 assign03.xi: Mismatch detected at line 14 of file assign03.parsed.nml
expected:   (g () (int) ((return 0)))))
found   :   (g () (int) ())))

---
# xic-ref (--parse [basic test]): assign03.xi
Mismatch detected at line 14 of file assign03.parsed.nml
expected:   (g () (int) ((return 0)))))
found   :   (g () (int) ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int[], y:int) {
  x[0] = 0
  x[y] = 0
  x[{1,2,3}[0]] = 0
  x[g()] = 0
  x[x[0]] = 0
  x[-1] = 0
  x[0+0] = 0
  x[(0)] = 0
}

g():int {
  return 0
}

```

## Generated result for --parse:

```
(()
 ((f
   ((x ([] int)) (y int))
   ()
   ((=
     ([] x 0) 0)
    (= ([] x y) 0)
    (= ([] x ([] (1 2 3) 0)) 0)
    (= ([] x (g)) 0)
    (= ([] x ([] x 0)) 0)
    (= ([] x (- 1)) 0)
    (= ([] x (+ 0 0)) 0)
    (= ([] x 0) 0)))
  (g () (int) ())))

```

## Expected result for --parse:

```
(()
 ((f
   ((x ([] int)) (y int))
   ()
   ((=
     ([] x 0) 0)
    (= ([] x y) 0)
    (= ([] x ([] (1 2 3) 0)) 0)
    (= ([] x (g)) 0)
    (= ([] x ([] x 0)) 0)
    (= ([] x (- 1)) 0)
    (= ([] x (+ 0 0)) 0)
    (= ([] x 0) 0)))
  (g () (int) ((return 0)))))

```

---
 assign04.xi: Mismatch detected at line 14 of file assign04.parsed.nml
expected:   (g1 () (int) ((return 0)))
found   :   (g1 () (int) ())

---
# xic-ref (--parse [basic test]): assign04.xi
Mismatch detected at line 14 of file assign04.parsed.nml
expected:   (g1 () (int) ((return 0)))
found   :   (g1 () (int) ())
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int[][], y:int, z:int) {
  x[0][1] = 0
  x[y][z] = 0
  x[{1,2,3}[0]][{1,2,3}[1]] = 0
  x[g1()][g2()] = 0
  x[x[0][1]][x[2][3]] = 0
  x[-1][-2] = 0
  x[0+0][0-0] = 0
  x[(0)][(1)] = 0
}

g1():int {
  return 0
}

g2():int {
  return 0
}

```

## Generated result for --parse:

```
(()
 ((f
   ((x ([] ([] int))) (y int) (z int))
   ()
   ((=
     ([] ([] x 0) 1) 0)
    (= ([] ([] x y) z) 0)
    (= ([] ([] x ([] (1 2 3) 0)) ([] (1 2 3) 1)) 0)
    (= ([] ([] x (g1)) (g2)) 0)
    (= ([] ([] x ([] ([] x 0) 1)) ([] ([] x 2) 3)) 0)
    (= ([] ([] x (- 1)) (- 2)) 0)
    (= ([] ([] x (+ 0 0)) (- 0 0)) 0)
    (= ([] ([] x 0) 1) 0)))
  (g1 () (int) ())
  (g2 () (int) ())))

```

## Expected result for --parse:

```
(()
 ((f
   ((x ([] ([] int))) (y int) (z int))
   ()
   ((=
     ([] ([] x 0) 1) 0)
    (= ([] ([] x y) z) 0)
    (= ([] ([] x ([] (1 2 3) 0)) ([] (1 2 3) 1)) 0)
    (= ([] ([] x (g1)) (g2)) 0)
    (= ([] ([] x ([] ([] x 0) 1)) ([] ([] x 2) 3)) 0)
    (= ([] ([] x (- 1)) (- 2)) 0)
    (= ([] ([] x (+ 0 0)) (- 0 0)) 0)
    (= ([] ([] x 0) 1) 0)))
  (g1 () (int) ((return 0)))
  (g2 () (int) ((return 0)))))

```

---
 block01.xi: OK
 block02.xi: Mismatch detected at line 2 of file block02.parsed.nml
expected:  ((f1 () (int) ((return 0)))
found   :  ((f1 () (int) ())

---
# xic-ref (--parse [basic test]): block02.xi
Mismatch detected at line 2 of file block02.parsed.nml
expected:  ((f1 () (int) ((return 0)))
found   :  ((f1 () (int) ())
## Command line without filenames:
xic --parse
## Content of test case:

```
f1(): int {
  return 0;
}

f2(): int {
  x:int
  return 0
}

f3(): int {
  x:int
  return 0;
}

f4(): int {
  x:int;
  return 0
}

f5(): int {
  x:int;
  return 0;
}

f6(): int {
  x01:int;
  x02:int
  x03:int;
  x04:int
  x05:int
  x06:int;
  x07:int
  x08:int
  x09:int
  x10:int;
  x11:int;
  x12:int
  return 0
}

f7(): int {
  x01:int;
  x02:int
  x03:int;
  x04:int
  x05:int
  x06:int;
  x07:int
  x08:int
  x09:int
  x10:int;
  x11:int;
  x12:int
  return 0;
}

```

## Generated result for --parse:

```
(()
 ((f1 () (int) ())
  (f2 () (int) ((x int) (return 0)))
  (f3 () (int) ((x int) (return 0)))
  (f4 () (int) ((x int) (return 0)))
  (f5 () (int) ((x int) (return 0)))
  (f6
   ()
   (int)
   ((x01 int)
    (x02 int)
    (x03 int)
    (x04 int)
    (x05 int)
    (x06 int)
    (x07 int)
    (x08 int)
    (x09 int)
    (x10 int)
    (x11 int)
    (x12 int)
    (return 0)))
  (f7
   ()
   (int)
   ((x01 int)
    (x02 int)
    (x03 int)
    (x04 int)
    (x05 int)
    (x06 int)
    (x07 int)
    (x08 int)
    (x09 int)
    (x10 int)
    (x11 int)
    (x12 int)
    (return 0)))))

```

## Expected result for --parse:

```
(()
 ((f1 () (int) ((return 0)))
  (f2 () (int) ((x int) (return 0)))
  (f3 () (int)
   ((x int) (return 0)))
  (f4 () (int)
   ((x int) (return 0)))
  (f5 () (int)
   ((x int) (return 0)))
  (f6
   ()
   (int)
   ((x01 int)
    (x02 int) (x03 int) (x04 int) (x05 int) (x06 int) (x07 int) (x08 int)
    (x09 int) (x10 int) (x11 int) (x12 int) (return 0)))
  (f7
   ()
   (int)
   ((x01 int)
    (x02 int) (x03 int) (x04 int) (x05 int) (x06 int) (x07 int) (x08 int)
    (x09 int) (x10 int) (x11 int) (x12 int) (return 0)))))

```

---
 call01.xi: OK
 call02.xi: OK
 codedecl01.xi: OK
 codedecl02.xi: Mismatch detected at line 1 of file codedecl02.parsed.nml
expected: (() ((f () (int) ((return 0)))))
found   : (() ((f () (int) ())))

---
# xic-ref (--parse [basic test]): codedecl02.xi
Mismatch detected at line 1 of file codedecl02.parsed.nml
expected: (() ((f () (int) ((return 0)))))
found   : (() ((f () (int) ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(): int {
  return 0
}

```

## Generated result for --parse:

```
(() ((f () (int) ())))

```

## Expected result for --parse:

```
(() ((f () (int) ((return 0)))))

```

---
 codedecl03.xi: Mismatch detected at line 2 of file codedecl03.parsed.nml
expected:  ((f1 () () ()) (f2 () (int) ((return 0))) (f3 () () ())
found   :  ((f1 () () ()) (f2 () (int) ()) (f3 () () ()) (f4 () (bool) ())

---
# xic-ref (--parse [basic test]): codedecl03.xi
Mismatch detected at line 2 of file codedecl03.parsed.nml
expected:  ((f1 () () ()) (f2 () (int) ((return 0))) (f3 () () ())
found   :  ((f1 () () ()) (f2 () (int) ()) (f3 () () ()) (f4 () (bool) ())
## Command line without filenames:
xic --parse
## Content of test case:

```
f1() {}
f2(): int {
  return 0
}
f3() {}
f4(): bool {
  return true
}
f5(): bool {
  return false
}
f6() {}

```

## Generated result for --parse:

```
(()
 ((f1 () () ())
  (f2 () (int) ())
  (f3 () () ())
  (f4 () (bool) ())
  (f5 () (bool) ())
  (f6 () () ())))

```

## Expected result for --parse:

```
(()
 ((f1 () () ()) (f2 () (int) ((return 0))) (f3 () () ())
  (f4 () (bool) ((return true))) (f5 () (bool) ((return false))) (f6 () () ())))

```

---
 codedecl04.xi: Mismatch detected at line 1 of file codedecl04.parsed.nml
expected: (() ((f1 ((x int)) () ()) (f2 ((x int)) (int) ((return 0)))))
found   : (() ((f1 ((x int)) () ()) (f2 ((x int)) (int) ())))

---
# xic-ref (--parse [basic test]): codedecl04.xi
Mismatch detected at line 1 of file codedecl04.parsed.nml
expected: (() ((f1 ((x int)) () ()) (f2 ((x int)) (int) ((return 0)))))
found   : (() ((f1 ((x int)) () ()) (f2 ((x int)) (int) ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f1(x: int) {}
f2(x: int): int {
  return 0
}

```

## Generated result for --parse:

```
(() ((f1 ((x int)) () ()) (f2 ((x int)) (int) ())))

```

## Expected result for --parse:

```
(() ((f1 ((x int)) () ()) (f2 ((x int)) (int) ((return 0)))))

```

---
 codedecl05.xi: Mismatch detected at line 3 of file codedecl05.parsed.nml
expected:   (f2 ((x1 int) (x2 bool) (x3 ([] int))) (int) ((return 0)))))
found   :   (f2 ((x1 int) (x2 bool) (x3 ([] int))) (int) ())))

---
# xic-ref (--parse [basic test]): codedecl05.xi
Mismatch detected at line 3 of file codedecl05.parsed.nml
expected:   (f2 ((x1 int) (x2 bool) (x3 ([] int))) (int) ((return 0)))))
found   :   (f2 ((x1 int) (x2 bool) (x3 ([] int))) (int) ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f1(x1: int, x2: bool, x3: int[]) {}
f2(x1: int, x2: bool, x3: int[]): int {
  return 0
}

```

## Generated result for --parse:

```
(()
 ((f1 ((x1 int) (x2 bool) (x3 ([] int))) () ())
  (f2 ((x1 int) (x2 bool) (x3 ([] int))) (int) ())))

```

## Expected result for --parse:

```
(()
 ((f1 ((x1 int) (x2 bool) (x3 ([] int))) () ())
  (f2 ((x1 int) (x2 bool) (x3 ([] int))) (int) ((return 0)))))

```

---
 codedecl06.xi: OK
 codedecl07.xi: Mismatch detected at line 1 of file codedecl07.parsed.nml
expected: (() ((f () (([] int)) (((return "hello"))))))
found   : (() ((f () (([] int)) (()))))

---
# xic-ref (--parse [basic test]): codedecl07.xi
Mismatch detected at line 1 of file codedecl07.parsed.nml
expected: (() ((f () (([] int)) (((return "hello"))))))
found   : (() ((f () (([] int)) (()))))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(): int[] {
  {
    return "hello";
  }
}

```

## Generated result for --parse:

```
(() ((f () (([] int)) (()))))

```

## Expected result for --parse:

```
(() ((f () (([] int)) (((return "hello"))))))

```

---
 expr01.xi: Mismatch detected at line 8 of file expr01.parsed.nml
expected:   (i () (int) ((return 0)))
found   :   (i () (int) ())

---
# xic-ref (--parse [basic test]): expr01.xi
Mismatch detected at line 8 of file expr01.parsed.nml
expected:   (i () (int) ((return 0)))
found   :   (i () (int) ())
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int, y:int[]) {
  x = 0
  x = 'a'
  x = x
  y = {1, 2, 3}
  x = i()
  x = y[0]
  x = -1
  x = 0 + 0
  x = (0)
}

i(): int {
  return 0
}

g(x:bool) {
  x = true
  x = false
}

h(x:int[]) {
  x = "hello"
}

```

## Generated result for --parse:

```
(()
 ((f
   ((x int) (y ([] int)))
   ()
   ((= x 0)
    (= x 'a')
    (= x x)
    (= y (1 2 3))
    (= x (i))
    (= x ([] y 0))
    (= x (- 1))
    (= x (+ 0 0))
    (= x 0)))
  (i () (int) ())
  (g ((x bool)) () ((= x true) (= x false)))
  (h ((x ([] int))) () ((= x "hello")))))

```

## Expected result for --parse:

```
(()
 ((f
   ((x int) (y ([] int)))
   ()
   ((= x 0)
    (= x 'a') (= x x) (= y (1 2 3)) (= x (i)) (= x ([] y 0)) (= x (- 1))
    (= x (+ 0 0)) (= x 0)))
  (i () (int) ((return 0)))
  (g ((x bool)) () ((= x true) (= x false)))
  (h ((x ([] int))) () ((= x "hello")))))

```

---
 expr02.xi: Mismatch detected at line 8 of file expr02.parsed.nml
expected:   (i () (int) ((return 0)))))
found   :   (i () (int) ())))

---
# xic-ref (--parse [basic test]): expr02.xi
Mismatch detected at line 8 of file expr02.parsed.nml
expected:   (i () (int) ((return 0)))))
found   :   (i () (int) ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int[]) {
  x = {}
  x = {1}
  x = {1,}
  x = {1,2}
  x = {1,2,}
  x = {1,2,3}
  x = {1,2,3,}
  x = {i(),1,i(),2,3,i(),4,5,6}
  x = {i(),1,i(),2,3,i(),4,5,6,}
}

i(): int {
  return 0
}

```

## Generated result for --parse:

```
(()
 ((f
   ((x ([] int)))
   ()
   ((= x ())
    (= x (1))
    (= x (1))
    (= x (1 2))
    (= x (1 2))
    (= x (1 2 3))
    (= x (1 2 3))
    (= x ((i) 1 (i) 2 3 (i) 4 5 6))
    (= x ((i) 1 (i) 2 3 (i) 4 5 6))))
  (i () (int) ())))

```

## Expected result for --parse:

```
(()
 ((f
   ((x ([] int)))
   ()
   ((= x ())
    (= x (1)) (= x (1)) (= x (1 2)) (= x (1 2)) (= x (1 2 3)) (= x (1 2 3))
    (= x ((i) 1 (i) 2 3 (i) 4 5 6)) (= x ((i) 1 (i) 2 3 (i) 4 5 6))))
  (i () (int) ((return 0)))))

```

---
 expr03.xi: OK
 expr04.xi: OK
 expr05.xi: Mismatch detected at line 14 of file expr05.parsed.nml
expected:   (i () (int) ((return 0)))))
found   :   (i () (int) ())))

---
# xic-ref (--parse [basic test]): expr05.xi
Mismatch detected at line 14 of file expr05.parsed.nml
expected:   (i () (int) ((return 0)))))
found   :   (i () (int) ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int, y:int[]) {
  x = {1,2,3}[0]
  x = {1,2,3}['a']
  x = {1,2,3}[x]
  x = {1,2,3}[i()]
  x = {1,2,3}[y[0]]
  x = {1,2,3}[-1]
  x = {1,2,3}[0+0]
  x = {1,2,3}[(0)]
}

i(): int {
  return 0
}

```

## Generated result for --parse:

```
(()
 ((f
   ((x int) (y ([] int)))
   ()
   ((= x
     ([] (1 2 3) 0))
    (= x ([] (1 2 3) 'a'))
    (= x ([] (1 2 3) x))
    (= x ([] (1 2 3) (i)))
    (= x ([] (1 2 3) ([] y 0)))
    (= x ([] (1 2 3) (- 1)))
    (= x ([] (1 2 3) (+ 0 0)))
    (= x ([] (1 2 3) 0))))
  (i () (int) ())))

```

## Expected result for --parse:

```
(()
 ((f
   ((x int) (y ([] int)))
   ()
   ((= x
     ([] (1 2 3) 0))
    (= x ([] (1 2 3) 'a'))
    (= x ([] (1 2 3) x))
    (= x ([] (1 2 3) (i)))
    (= x ([] (1 2 3) ([] y 0)))
    (= x ([] (1 2 3) (- 1)))
    (= x ([] (1 2 3) (+ 0 0)))
    (= x ([] (1 2 3) 0))))
  (i () (int) ((return 0)))))

```

---
 expr06.xi: Mismatch detected at line 13 of file expr06.parsed.nml
expected:     (= x (- 9223372036854775808))))
found   :     (= x -9223372036854775808)))

---
# xic-ref (--parse [basic test]): expr06.xi
Mismatch detected at line 13 of file expr06.parsed.nml
expected:     (= x (- 9223372036854775808))))
found   :     (= x -9223372036854775808)))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int, y:int[]) {
  x = -0
  x = -'a'
  x = -x
  x = -i()
  x = -y[0]
  x = - -1
  x = (0)
  x = -9223372036854775808
}

i(): int {
  return 0
}

g(x:bool) {
  x = !true
  x = !false
}

```

## Generated result for --parse:

```
(()
 ((f
   ((x int) (y ([] int)))
   ()
   ((= x
     (- 0))
    (= x (- 'a'))
    (= x (- x))
    (= x (- (i)))
    (= x (- ([] y 0)))
    (= x (- (- 1)))
    (= x 0)
    (= x -9223372036854775808)))
  (i () (int) ())
  (g ((x bool)) () ((= x (! true)) (= x (! false))))))

```

## Expected result for --parse:

```
(()
 ((f
   ((x int) (y ([] int)))
   ()
   ((= x
     (- 0))
    (= x (- 'a'))
    (= x (- x))
    (= x (- (i)))
    (= x (- ([] y 0)))
    (= x (- (- 1)))
    (= x 0)
    (= x (- 9223372036854775808))))
  (i () (int) ((return 0)))
  (g ((x bool)) () ((= x (! true)) (= x (! false))))))

```

---
 expr07.xi: OK
 expr08.xi: Mismatch detected at line 78 of file expr08.parsed.nml
expected:   (i () (int) ((return 0)))
found   :   (i () (int) ())

---
# xic-ref (--parse [basic test]): expr08.xi
Mismatch detected at line 78 of file expr08.parsed.nml
expected:   (i () (int) ((return 0)))
found   :   (i () (int) ())
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int, y:int[]) {
  x = 0 + 'a'
  x = 0 - 'a'
  x = 0 * 'a'
  x = 0 *>> 'a'
  x = 0 / 'a'
  x = 0 % 'a'

  x = 0 + x
  x = 0 - x
  x = 0 * x
  x = 0 *>> x
  x = 0 / x
  x = 0 % x

  x = 0 + i()
  x = 0 - i()
  x = 0 * i()
  x = 0 *>> i()
  x = 0 / i()
  x = 0 % i()

  x = 0 + y[0]
  x = 0 - y[0]
  x = 0 * y[0]
  x = 0 *>> y[0]
  x = 0 / y[0]
  x = 0 % y[0]

  x = 0 + -1
  x = 0 - -1
  x = 0 * -1
  x = 0 *>> -1
  x = 0 / -1
  x = 0 % -1

  x = 0 + (0)
  x = 0 - (0)
  x = 0 * (0)
  x = 0 *>> (0)
  x = 0 / (0)
  x = 0 % (0)

  x = 'a' + 1
  x = 'a' - 1
  x = 'a' * 1
  x = 'a' *>> 1
  x = 'a' / 1
  x = 'a' % 1

  x = x + 1
  x = x - 1
  x = x * 1
  x = x *>> 1
  x = x / 1
  x = x % 1

  x = i() + 1
  x = i() - 1
  x = i() * 1
  x = i() *>> 1
  x = i() / 1
  x = i() % 1

  x = y[0] + 1
  x = y[0] - 1
  x = y[0] * 1
  x = y[0] *>> 1
  x = y[0] / 1
  x = y[0] % 1

  x = -1 + 1
  x = -1 - 1
  x = -1 * 1
  x = -1 *>> 1
  x = -1 / 1
  x = -1 % 1

  x = (0) + 1
  x = (0) - 1
  x = (0) * 1
  x = (0) *>> 1
  x = (0) / 1
  x = (0) % 1
}

i(): int {
  return 0
}

g(x:bool, y:int, a1:bool[], a2:int[]) {
  x = 0 < 'a'
  x = 0 <= 'a'
  x = 0 >= 'a'
  x = 0 > 'a'
  x = 0 == 'a'
  x = 0 != 'a'

  x = 0 < y
  x = 0 <= y
  x = 0 >= y
  x = 0 > y
  x = 0 == y
  x = 0 != y
  x = true == x
  x = true != x
  x = true & x
  x = false | x

  x = 0 < i()
  x = 0 <= i()
  x = 0 >= i()
  x = 0 > i()
  x = 0 == i()
  x = 0 != i()
  x = true == b()
  x = true != b()
  x = true & b()
  x = false | b()

  x = 0 < a2[0]
  x = 0 <= a2[0]
  x = 0 >= a2[0]
  x = 0 > a2[0]
  x = 0 == a2[0]
  x = 0 != a2[0]
  x = true == a1[0]
  x = true != a1[0]
  x = true & a1[0]
  x = false | a1[0]

  x = 0 < -1
  x = 0 <= -1
  x = 0 >= -1
  x = 0 > -1
  x = 0 == -1
  x = 0 != -1

  x = 0 < 0+0
  x = 0 <= 0+0
  x = 0 >= 0+0
  x = 0 > 0+0
  x = 0 == 0+0
  x = 0 != 0+0

  x = 0 < (0)
  x = 0 <= (0)
  x = 0 >= (0)
  x = 0 > (0)
  x = 0 == (0)
  x = 0 != (0)
  x = true == (false)
  x = true != (false)
  x = true & (false)
  x = false | (false)

  x = 'a' < 1
  x = 'a' <= 1
  x = 'a' >= 1
  x = 'a' > 1
  x = 'a' == 1
  x = 'a' != 1

  x = y < 1
  x = y <= 1
  x = y >= 1
  x = y > 1
  x = y == 1
  x = y != 1
  x = x == false
  x = x != false
  x = x & false
  x = x | true

  x = i() < 1
  x = i() <= 1
  x = i() >= 1
  x = i() > 1
  x = i() == 1
  x = i() != 1
  x = b() == false
  x = b() != false
  x = b() & false
  x = b() | true

  x = a2[0] < 1
  x = a2[0] <= 1
  x = a2[0] >= 1
  x = a2[0] > 1
  x = a2[0] == 1
  x = a2[0] != 1
  x = a1[0] == false
  x = a1[0] != false
  x = a1[0] & false
  x = a1[0] | true

  x = -1 < 1
  x = -1 <= 1
  x = -1 >= 1
  x = -1 > 1
  x = -1 == 1
  x = -1 != 1

  x = 0+0 < 1
  x = 0+0 <= 1
  x = 0+0 >= 1
  x = 0+0 > 1
  x = 0+0 == 1
  x = 0+0 != 1

  x = (0) < 1
  x = (0) <= 1
  x = (0) >= 1
  x = (0) > 1
  x = (0) == 1
  x = (0) != 1
  x = (false) == false
  x = (false) != false
  x = (true) & false
  x = (false) | true
}

b(): bool {
  return false
}

h(x:int[]) {
  x = x + x
  x = x + {2,3}
  x = {1} + x
  x = {1} + {2,3}

  x = x + (x)
  x = x + ({2,3})
  x = {1} + (x)
  x = {1} + ({2,3})

  x = (x) + x
  x = (x) + {2,3}
  x = ({1}) + x
  x = ({1}) + {2,3}
}

```

## Generated result for --parse:

```
(()
 ((f
   ((x int) (y ([] int)))
   ()
   ((= x
     (+ 0 'a'))
    (= x (- 0 'a'))
    (= x (* 0 'a'))
    (= x (*>> 0 'a'))
    (= x (/ 0 'a'))
    (= x (% 0 'a'))
    (= x (+ 0 x))
    (= x (- 0 x))
    (= x (* 0 x))
    (= x (*>> 0 x))
    (= x (/ 0 x))
    (= x (% 0 x))
    (= x (+ 0 (i)))
    (= x (- 0 (i)))
    (= x (* 0 (i)))
    (= x (*>> 0 (i)))
    (= x (/ 0 (i)))
    (= x (% 0 (i)))
    (= x (+ 0 ([] y 0)))
    (= x (- 0 ([] y 0)))
    (= x (* 0 ([] y 0)))
    (= x (*>> 0 ([] y 0)))
    (= x (/ 0 ([] y 0)))
    (= x (% 0 ([] y 0)))
    (= x (+ 0 (- 1)))
    (= x (- 0 (- 1)))
    (= x (* 0 (- 1)))
    (= x (*>> 0 (- 1)))
    (= x (/ 0 (- 1)))
    (= x (% 0 (- 1)))
    (= x (+ 0 0))
    (= x (- 0 0))
    (= x (* 0 0))
    (= x (*>> 0 0))
    (= x (/ 0 0))
    (= x (% 0 0))
    (= x (+ 'a' 1))
    (= x (- 'a' 1))
    (= x (* 'a' 1))
    (= x (*>> 'a' 1))
    (= x (/ 'a' 1))
    (= x (% 'a' 1))
    (= x (+ x 1))
    (= x (- x 1))
    (= x (* x 1))
    (= x (*>> x 1))
    (= x (/ x 1))
    (= x (% x 1))
    (= x (+ (i) 1))
    (= x (- (i) 1))
    (= x (* (i) 1))
    (= x (*>> (i) 1))
    (= x (/ (i) 1))
    (= x (% (i) 1))
    (= x (+ ([] y 0) 1))
    (= x (- ([] y 0) 1))
    (= x (* ([] y 0) 1))
    (= x (*>> ([] y 0) 1))
    (= x (/ ([] y 0) 1))
    (= x (% ([] y 0) 1))
    (= x (+ (- 1) 1))
    (= x (- (- 1) 1))
    (= x (* (- 1) 1))
    (= x (*>> (- 1) 1))
    (= x (/ (- 1) 1))
    (= x (% (- 1) 1))
    (= x (+ 0 1))
    (= x (- 0 1))
    (= x (* 0 1))
    (= x (*>> 0 1))
    (= x (/ 0 1))
    (= x (% 0 1))))
  (i () (int) ())
  (g
   ((x bool) (y int) (a1 ([] bool)) (a2 ([] int)))
   ()
   ((= x
     (< 0 'a'))
    (= x (<= 0 'a'))
    (= x (>= 0 'a'))
    (= x (> 0 'a'))
    (= x (== 0 'a'))
    (= x (!= 0 'a'))
    (= x (< 0 y))
    (= x (<= 0 y))
    (= x (>= 0 y))
    (= x (> 0 y))
    (= x (== 0 y))
    (= x (!= 0 y))
    (= x (== true x))
    (= x (!= true x))
    (= x (& true x))
    (= x (| false x))
    (= x (< 0 (i)))
    (= x (<= 0 (i)))
    (= x (>= 0 (i)))
    (= x (> 0 (i)))
    (= x (== 0 (i)))
    (= x (!= 0 (i)))
    (= x (== true (b)))
    (= x (!= true (b)))
    (= x (& true (b)))
    (= x (| false (b)))
    (= x (< 0 ([] a2 0)))
    (= x (<= 0 ([] a2 0)))
    (= x (>= 0 ([] a2 0)))
    (= x (> 0 ([] a2 0)))
    (= x (== 0 ([] a2 0)))
    (= x (!= 0 ([] a2 0)))
    (= x (== true ([] a1 0)))
    (= x (!= true ([] a1 0)))
    (= x (& true ([] a1 0)))
    (= x (| false ([] a1 0)))
    (= x (< 0 (- 1)))
    (= x (<= 0 (- 1)))
    (= x (>= 0 (- 1)))
    (= x (> 0 (- 1)))
    (= x (== 0 (- 1)))
    (= x (!= 0 (- 1)))
    (= x (< 0 (+ 0 0)))
    (= x (<= 0 (+ 0 0)))
    (= x (>= 0 (+ 0 0)))
    (= x (> 0 (+ 0 0)))
    (= x (== 0 (+ 0 0)))
    (= x (!= 0 (+ 0 0)))
    (= x (< 0 0))
    (= x (<= 0 0))
    (= x (>= 0 0))
    (= x (> 0 0))
    (= x (== 0 0))
    (= x (!= 0 0))
    (= x (== true false))
    (= x (!= true false))
    (= x (& true false))
    (= x (| false false))
    (= x (< 'a' 1))
    (= x (<= 'a' 1))
    (= x (>= 'a' 1))
    (= x (> 'a' 1))
    (= x (== 'a' 1))
    (= x (!= 'a' 1))
    (= x (< y 1))
    (= x (<= y 1))
    (= x (>= y 1))
    (= x (> y 1))
    (= x (== y 1))
    (= x (!= y 1))
    (= x (== x false))
    (= x (!= x false))
    (= x (& x false))
    (= x (| x true))
    (= x (< (i) 1))
    (= x (<= (i) 1))
    (= x (>= (i) 1))
    (= x (> (i) 1))
    (= x (== (i) 1))
    (= x (!= (i) 1))
    (= x (== (b) false))
    (= x (!= (b) false))
    (= x (& (b) false))
    (= x (| (b) true))
    (= x (< ([] a2 0) 1))
    (= x (<= ([] a2 0) 1))
    (= x (>= ([] a2 0) 1))
    (= x (> ([] a2 0) 1))
    (= x (== ([] a2 0) 1))
    (= x (!= ([] a2 0) 1))
    (= x (== ([] a1 0) false))
    (= x (!= ([] a1 0) false))
    (= x (& ([] a1 0) false))
    (= x (| ([] a1 0) true))
    (= x (< (- 1) 1))
    (= x (<= (- 1) 1))
    (= x (>= (- 1) 1))
    (= x (> (- 1) 1))
    (= x (== (- 1) 1))
    (= x (!= (- 1) 1))
    (= x (< (+ 0 0) 1))
    (= x (<= (+ 0 0) 1))
    (= x (>= (+ 0 0) 1))
    (= x (> (+ 0 0) 1))
    (= x (== (+ 0 0) 1))
    (= x (!= (+ 0 0) 1))
    (= x (< 0 1))
    (= x (<= 0 1))
    (= x (>= 0 1))
    (= x (> 0 1))
    (= x (== 0 1))
    (= x (!= 0 1))
    (= x (== false false))
    (= x (!= false false))
    (= x (& true false))
    (= x (| false true))))
  (b () (bool) ())
  (h
   ((x ([] int)))
   ()
   ((= x
     (+ x x))
    (= x (+ x (2 3)))
    (= x (+ (1) x))
    (= x (+ (1) (2 3)))
    (= x (+ x x))
    (= x (+ x (2 3)))
    (= x (+ (1) x))
    (= x (+ (1) (2 3)))
    (= x (+ x x))
    (= x (+ x (2 3)))
    (= x (+ (1) x))
    (= x (+ (1) (2 3)))))))

```

## Expected result for --parse:

```
(()
 ((f
   ((x int) (y ([] int)))
   ()
   ((= x
     (+ 0 'a'))
    (= x (- 0 'a'))
    (= x (* 0 'a'))
    (= x (*>> 0 'a'))
    (= x (/ 0 'a'))
    (= x (% 0 'a'))
    (= x (+ 0 x))
    (= x (- 0 x))
    (= x (* 0 x))
    (= x (*>> 0 x))
    (= x (/ 0 x))
    (= x (% 0 x))
    (= x (+ 0 (i)))
    (= x (- 0 (i)))
    (= x (* 0 (i)))
    (= x (*>> 0 (i)))
    (= x (/ 0 (i)))
    (= x (% 0 (i)))
    (= x (+ 0 ([] y 0)))
    (= x (- 0 ([] y 0)))
    (= x (* 0 ([] y 0)))
    (= x (*>> 0 ([] y 0)))
    (= x (/ 0 ([] y 0)))
    (= x (% 0 ([] y 0)))
    (= x (+ 0 (- 1)))
    (= x (- 0 (- 1)))
    (= x (* 0 (- 1)))
    (= x (*>> 0 (- 1)))
    (= x (/ 0 (- 1)))
    (= x (% 0 (- 1)))
    (= x (+ 0 0))
    (= x (- 0 0))
    (= x (* 0 0))
    (= x (*>> 0 0))
    (= x (/ 0 0))
    (= x (% 0 0))
    (= x (+ 'a' 1))
    (= x (- 'a' 1))
    (= x (* 'a' 1))
    (= x (*>> 'a' 1))
    (= x (/ 'a' 1))
    (= x (% 'a' 1))
    (= x (+ x 1))
    (= x (- x 1))
    (= x (* x 1))
    (= x (*>> x 1))
    (= x (/ x 1))
    (= x (% x 1))
    (= x (+ (i) 1))
    (= x (- (i) 1))
    (= x (* (i) 1))
    (= x (*>> (i) 1))
    (= x (/ (i) 1))
    (= x (% (i) 1))
    (= x (+ ([] y 0) 1))
    (= x (- ([] y 0) 1))
    (= x (* ([] y 0) 1))
    (= x (*>> ([] y 0) 1))
    (= x (/ ([] y 0) 1))
    (= x (% ([] y 0) 1))
    (= x (+ (- 1) 1))
    (= x (- (- 1) 1))
    (= x (* (- 1) 1))
    (= x (*>> (- 1) 1))
    (= x (/ (- 1) 1))
    (= x (% (- 1) 1))
    (= x (+ 0 1))
    (= x (- 0 1))
    (= x (* 0 1))
    (= x (*>> 0 1))
    (= x (/ 0 1))
    (= x (% 0 1))))
  (i () (int) ((return 0)))
  (g
   ((x bool) (y int) (a1 ([] bool)) (a2 ([] int)))
   ()
   ((= x
     (< 0 'a'))
    (= x (<= 0 'a'))
    (= x (>= 0 'a'))
    (= x (> 0 'a'))
    (= x (== 0 'a'))
    (= x (!= 0 'a'))
    (= x (< 0 y))
    (= x (<= 0 y))
    (= x (>= 0 y))
    (= x (> 0 y))
    (= x (== 0 y))
    (= x (!= 0 y))
    (= x (== true x))
    (= x (!= true x))
    (= x (& true x))
    (= x (| false x))
    (= x (< 0 (i)))
    (= x (<= 0 (i)))
    (= x (>= 0 (i)))
    (= x (> 0 (i)))
    (= x (== 0 (i)))
    (= x (!= 0 (i)))
    (= x (== true (b)))
    (= x (!= true (b)))
    (= x (& true (b)))
    (= x (| false (b)))
    (= x (< 0 ([] a2 0)))
    (= x (<= 0 ([] a2 0)))
    (= x (>= 0 ([] a2 0)))
    (= x (> 0 ([] a2 0)))
    (= x (== 0 ([] a2 0)))
    (= x (!= 0 ([] a2 0)))
    (= x (== true ([] a1 0)))
    (= x (!= true ([] a1 0)))
    (= x (& true ([] a1 0)))
    (= x (| false ([] a1 0)))
    (= x (< 0 (- 1)))
    (= x (<= 0 (- 1)))
    (= x (>= 0 (- 1)))
    (= x (> 0 (- 1)))
    (= x (== 0 (- 1)))
    (= x (!= 0 (- 1)))
    (= x (< 0 (+ 0 0)))
    (= x (<= 0 (+ 0 0)))
    (= x (>= 0 (+ 0 0)))
    (= x (> 0 (+ 0 0)))
    (= x (== 0 (+ 0 0)))
    (= x (!= 0 (+ 0 0)))
    (= x (< 0 0))
    (= x (<= 0 0))
    (= x (>= 0 0))
    (= x (> 0 0))
    (= x (== 0 0))
    (= x (!= 0 0))
    (= x (== true false))
    (= x (!= true false))
    (= x (& true false))
    (= x (| false false))
    (= x (< 'a' 1))
    (= x (<= 'a' 1))
    (= x (>= 'a' 1))
    (= x (> 'a' 1))
    (= x (== 'a' 1))
    (= x (!= 'a' 1))
    (= x (< y 1))
    (= x (<= y 1))
    (= x (>= y 1))
    (= x (> y 1))
    (= x (== y 1))
    (= x (!= y 1))
    (= x (== x false))
    (= x (!= x false))
    (= x (& x false))
    (= x (| x true))
    (= x (< (i) 1))
    (= x (<= (i) 1))
    (= x (>= (i) 1))
    (= x (> (i) 1))
    (= x (== (i) 1))
    (= x (!= (i) 1))
    (= x (== (b) false))
    (= x (!= (b) false))
    (= x (& (b) false))
    (= x (| (b) true))
    (= x (< ([] a2 0) 1))
    (= x (<= ([] a2 0) 1))
    (= x (>= ([] a2 0) 1))
    (= x (> ([] a2 0) 1))
    (= x (== ([] a2 0) 1))
    (= x (!= ([] a2 0) 1))
    (= x (== ([] a1 0) false))
    (= x (!= ([] a1 0) false))
    (= x (& ([] a1 0) false))
    (= x (| ([] a1 0) true))
    (= x (< (- 1) 1))
    (= x (<= (- 1) 1))
    (= x (>= (- 1) 1))
    (= x (> (- 1) 1))
    (= x (== (- 1) 1))
    (= x (!= (- 1) 1))
    (= x (< (+ 0 0) 1))
    (= x (<= (+ 0 0) 1))
    (= x (>= (+ 0 0) 1))
    (= x (> (+ 0 0) 1))
    (= x (== (+ 0 0) 1))
    (= x (!= (+ 0 0) 1))
    (= x (< 0 1))
    (= x (<= 0 1))
    (= x (>= 0 1))
    (= x (> 0 1))
    (= x (== 0 1))
    (= x (!= 0 1))
    (= x (== false false))
    (= x (!= false false))
    (= x (& true false))
    (= x (| false true))))
  (b () (bool) ((return false)))
  (h
   ((x ([] int)))
   ()
   ((= x
     (+ x x))
    (= x (+ x (2 3)))
    (= x (+ (1) x))
    (= x (+ (1) (2 3)))
    (= x (+ x x))
    (= x (+ x (2 3)))
    (= x (+ (1) x))
    (= x (+ (1) (2 3)))
    (= x (+ x x))
    (= x (+ x (2 3)))
    (= x (+ (1) x))
    (= x (+ (1) (2 3)))))))

```

---
 if01.xi: Mismatch detected at line 7 of file if01.parsed.nml
expected:   (g () (bool) ((return true)))))
found   :   (g () (bool) ())))

---
# xic-ref (--parse [basic test]): if01.xi
Mismatch detected at line 7 of file if01.parsed.nml
expected:   (g () (bool) ((return true)))))
found   :   (g () (bool) ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f() {}

f1() {
  if (true) f()
  if (false) f()
  if (g()) f()
  if ((true)) f()
  if ((g())) f()
}

f2(b:bool) {
  if (b) f()
}

f3(b1:bool, b2:bool) {
  if (b1 & b2) f()
  if (b1 | b2) f()
}

g(): bool {
  return true
}

```

## Generated result for --parse:

```
(()
 ((f () () ())
  (f1 () ()
   ((if true (f)) (if false (f)) (if (g) (f)) (if true (f)) (if (g) (f))))
  (f2 ((b bool)) () ((if b (f))))
  (f3 ((b1 bool) (b2 bool)) () ((if (& b1 b2) (f)) (if (| b1 b2) (f))))
  (g () (bool) ())))

```

## Expected result for --parse:

```
(()
 ((f () () ())
  (f1 () ()
   ((if true (f)) (if false (f)) (if (g) (f)) (if true (f)) (if (g) (f))))
  (f2 ((b bool)) () ((if b (f))))
  (f3 ((b1 bool) (b2 bool)) () ((if (& b1 b2) (f)) (if (| b1 b2) (f))))
  (g () (bool) ((return true)))))

```

---
 if02.xi: Mismatch detected at line 12 of file if02.parsed.nml
expected:     (if true ((return)))
found   :     (if true ())

---
# xic-ref (--parse [basic test]): if02.xi
Mismatch detected at line 12 of file if02.parsed.nml
expected:     (if true ((return)))
found   :     (if true ())
## Command line without filenames:
xic --parse
## Content of test case:

```
f() {}

f1() {
  if (true) if (true) f()
  if (true) if (true) f() else f()
  if (true) while (true) f()
  if (true) f()
  if (true) {}
  if (true) { return }
  if (true) x:int[0]
}

f2(x:int, y:int) {
  if (true) x = y
}

```

## Generated result for --parse:

```
(()
 ((f () () ())
  (f1
   ()
   ()
   ((if
     true
     (if true (f)))
    (if true (if true (f) (f)))
    (if true (while true (f)))
    (if true (f))
    (if true ())
    (if true ())
    (if true (x ([] int 0)))))
  (f2 ((x int) (y int)) () ((if true (= x y))))))

```

## Expected result for --parse:

```
(()
 ((f () () ())
  (f1
   ()
   ()
   ((if true
     (if true (f)))
    (if true (if true (f) (f)))
    (if true (while true (f)))
    (if true (f))
    (if true ())
    (if true ((return)))
    (if true (x ([] int 0)))))
  (f2 ((x int) (y int)) () ((if true (= x y))))))

```

---
 if03.xi: OK
 if04.xi: OK
 length01.xi: OK
 prec01.xi: OK
 prec02.xi: OK
 prec03.xi: OK
 prec04.xi: OK
 prec05.xi: OK
 prec06.xi: OK
 prec07.xi: Mismatch detected at line 4 of file prec07.parsed.nml
expected:   (g () (([] int)) ((return (1))))))
found   :   (g () (([] int)) ())))

---
# xic-ref (--parse [basic test]): prec07.xi
Mismatch detected at line 4 of file prec07.parsed.nml
expected:   (g () (([] int)) ((return (1))))))
found   :   (g () (([] int)) ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int, y:int[]) {
  x = -y[0]
  x = -{1,2,3}[0]
  x = -g()[0]
}

g(): int[] {
  return {1}
}

```

## Generated result for --parse:

```
(()
 ((f ((x int) (y ([] int))) ()
   ((= x (- ([] y 0))) (= x (- ([] (1 2 3) 0))) (= x (- ([] (g) 0)))))
  (g () (([] int)) ())))

```

## Expected result for --parse:

```
(()
 ((f ((x int) (y ([] int))) ()
   ((= x (- ([] y 0))) (= x (- ([] (1 2 3) 0))) (= x (- ([] (g) 0)))))
  (g () (([] int)) ((return (1))))))

```

---
 return01.xi: Mismatch detected at line 2 of file return01.parsed.nml
expected:  ((f1 () (bool bool bool bool)
found   :  ((f1 () (bool bool bool bool) ())

---
# xic-ref (--parse [basic test]): return01.xi
Mismatch detected at line 2 of file return01.parsed.nml
expected:  ((f1 () (bool bool bool bool)
found   :  ((f1 () (bool bool bool bool) ())
## Command line without filenames:
xic --parse
## Content of test case:

```
f1(): bool, bool, bool, bool {
  return true, false, true & false, false | true
}

f2(): int, int, int, int {
  x: int
  return 0, x, 1-x, x*2;
}

f3(): int[], int[][], int[][][] {
  return {0}, {{0}}, {{{0}}}
}

```

## Generated result for --parse:

```
(()
 ((f1 () (bool bool bool bool) ())
  (f2 () (int int int int) ((x int) (return 0 x (- 1 x) (* x 2))))
  (f3 () (([] int) ([] ([] int)) ([] ([] ([] int)))) ())))

```

## Expected result for --parse:

```
(()
 ((f1 () (bool bool bool bool)
   ((return true false (& true false) (| false true))))
  (f2 () (int int int int) ((x int) (return 0 x (- 1 x) (* x 2))))
  (f3 () (([] int) ([] ([] int)) ([] ([] ([] int))))
   ((return (0) ((0)) (((0))))))))

```

---
 return02.xi: Mismatch detected at line 1 of file return02.parsed.nml
expected: (() ((f1 () () ((return))) (f2 () () ((return)))))
found   : (() ((f1 () () ()) (f2 () () ())))

---
# xic-ref (--parse [basic test]): return02.xi
Mismatch detected at line 1 of file return02.parsed.nml
expected: (() ((f1 () () ((return))) (f2 () () ((return)))))
found   : (() ((f1 () () ()) (f2 () () ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f1() {
  return
}

f2() {
  return;
}

```

## Generated result for --parse:

```
(() ((f1 () () ()) (f2 () () ())))

```

## Expected result for --parse:

```
(() ((f1 () () ((return))) (f2 () () ((return)))))

```

---
 use01.xi: OK
 use02.xi: OK
 use03.xi: OK
 vardecl01.xi: OK
 vardecl02.xi: OK
 vardecl03.xi: Mismatch detected at line 1 of file vardecl03.parsed.nml
expected: (()
found   : 3:6 error:

---
# xic-ref (--parse [basic test]): vardecl03.xi
Mismatch detected at line 1 of file vardecl03.parsed.nml
expected: (()
found   : 3:6 error:
## Command line without filenames:
xic --parse
## Content of test case:

```
f() {
  _ = g1()
  _, _ = g2()
  _, x032:int = g2()
  x041:int, _ = g2()
  x051:int, x052:int = g2()
  _, _, _ = g3()
  _, _, x073:int = g3()
  _, x082:int, _ = g3()
  x091:int, _, _ = g3()
  x101:int, x102:int, _ = g3()
  x111:int, _, x113:int = g3()
  _, x122:int, x123:int = g3()
  x131:int, x132:int, x133:int = g3()
}

g1():int {
  return 0
}

g2():int, int {
  return 0, 0
}

g3():int, int {
  return 0, 0, 0
}

```

## Compiler's standard output:
Syntax Error
3:6 error:Unexpected token _

## Generated result for --parse:

```
3:6 error:Unexpected token _

```

## Expected result for --parse:

```
(()
 ((f
   ()
   ()
   ((= _ (g1))
    (= (_ _) (g2)) (= (_ (x032 int)) (g2)) (= ((x041 int) _) (g2))
    (= ((x051 int) (x052 int)) (g2)) (= (_ _ _) (g3)) (= (_ _ (x073 int)) (g3))
    (= (_ (x082 int) _) (g3)) (= ((x091 int) _ _) (g3))
    (= ((x101 int) (x102 int) _) (g3)) (= ((x111 int) _ (x113 int)) (g3))
    (= (_ (x122 int) (x123 int)) (g3))
    (= ((x131 int) (x132 int) (x133 int)) (g3))))
  (g1 () (int) ((return 0)))
  (g2 () (int int) ((return 0 0)))
  (g3 () (int int) ((return 0 0 0)))))

```

---
 vardecl04.xi: Mismatch detected at line 16 of file vardecl04.parsed.nml
expected:   (g1 () (int) ((return 0)))))
found   :   (g1 () (int) ())))

---
# xic-ref (--parse [basic test]): vardecl04.xi
Mismatch detected at line 16 of file vardecl04.parsed.nml
expected:   (g1 () (int) ((return 0)))))
found   :   (g1 () (int) ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f() {
  x01:int = 0
  x02:bool = true
  x03:int = x01
  x04:int[] = {1, 2, 3}
  x05:int = g1()
  x06:int = x04[0]
  x07:int = -1
  x08:int = 0 + 0
  x09:int = (0)
  x10:int[][] = {{1}, {2, 3}}
}

g1():int {
  return 0
}

```

## Generated result for --parse:

```
(()
 ((f
   ()
   ()
   ((=
     (x01 int) 0)
    (= (x02 bool) true)
    (= (x03 int) x01)
    (= (x04 ([] int)) (1 2 3))
    (= (x05 int) (g1))
    (= (x06 int) ([] x04 0))
    (= (x07 int) (- 1))
    (= (x08 int) (+ 0 0))
    (= (x09 int) 0)
    (= (x10 ([] ([] int))) ((1) (2 3)))))
  (g1 () (int) ())))

```

## Expected result for --parse:

```
(()
 ((f
   ()
   ()
   ((=
     (x01 int) 0)
    (= (x02 bool) true)
    (= (x03 int) x01)
    (= (x04 ([] int)) (1 2 3))
    (= (x05 int) (g1))
    (= (x06 int) ([] x04 0))
    (= (x07 int) (- 1))
    (= (x08 int) (+ 0 0))
    (= (x09 int) 0)
    (= (x10 ([] ([] int))) ((1) (2 3)))))
  (g1 () (int) ((return 0)))))

```

---
 while01.xi: OK
 while02.xi: OK
 xic-ref (--parse [basic test]): 24 out of 46 tests succeeded.
 Test collection: xic-ref (--parse [basic-error test])
 assign01.xi: OK
 assign02.xi: OK
 assign03.xi: OK
 assign04.xi: OK
 block01.xi: OK
 block02.xi: OK
 call01.xi: OK
 call02.xi: OK
 call03.xi: OK
 codedecl01.xi: Mismatch detected at line 1 of file codedecl01.parsed.nml
expected: 2:1 error:
found   : (((f () ())))

---
# xic-ref (--parse [basic-error test]): codedecl01.xi
Mismatch detected at line 1 of file codedecl01.parsed.nml
expected: 2:1 error:
found   : (((f () ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f()

```

## Generated result for --parse:

```
(((f () ())))

```

## Expected result for --parse:

```
2:1 error:Syntax error: unexpected end of file.

```

---
 codedecl02.xi: Mismatch detected at line 1 of file codedecl02.parsed.nml
expected: 2:1 error:
found   : (((f () (int))))

---
# xic-ref (--parse [basic-error test]): codedecl02.xi
Mismatch detected at line 1 of file codedecl02.parsed.nml
expected: 2:1 error:
found   : (((f () (int))))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(): int

```

## Generated result for --parse:

```
(((f () (int))))

```

## Expected result for --parse:

```
2:1 error:Syntax error: unexpected end of file.

```

---
 codedecl03.xi: Mismatch detected at line 1 of file codedecl03.parsed.nml
expected: 2:1 error:
found   : (((f1 () (int)) (f2 () ())))

---
# xic-ref (--parse [basic-error test]): codedecl03.xi
Mismatch detected at line 1 of file codedecl03.parsed.nml
expected: 2:1 error:
found   : (((f1 () (int)) (f2 () ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f1(): int
f2()

```

## Generated result for --parse:

```
(((f1 () (int)) (f2 () ())))

```

## Expected result for --parse:

```
2:1 error:Syntax error: unexpected id f2.

```

---
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
 length03.xi: Mismatch detected at line 1 of file length03.parsed.nml
expected: 3:1 error:
found   : 2:3 error:

// TODO: start adding tests after this
---
# xic-ref (--parse [basic-error test]): length03.xi
Mismatch detected at line 1 of file length03.parsed.nml
expected: 3:1 error:
found   : 2:3 error:
## Command line without filenames:
xic --parse
## Content of test case:

```
f() {
  length(a)
}

```

## Compiler's standard output:
Syntax Error
2:3 error:Unexpected token length

## Generated result for --parse:

```
2:3 error:Unexpected token length

```

## Expected result for --parse:

```
3:1 error:Syntax error: unexpected }.

```

---
 paramdecl01.xi: Mismatch detected at line 1 of file paramdecl01.parsed.nml
expected: 1:9 error:
found   : (() ((f ((x ([] int 1))) () ())))

---
# xic-ref (--parse [basic-error test]): paramdecl01.xi
Mismatch detected at line 1 of file paramdecl01.parsed.nml
expected: 1:9 error:
found   : (() ((f ((x ([] int 1))) () ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int[1]) {}

```

## Generated result for --parse:

```
(() ((f ((x ([] int 1))) () ())))

```

## Expected result for --parse:

```
1:9 error:Syntax error: unexpected integer 1.

```

---
 paramdecl02.xi: Mismatch detected at line 1 of file paramdecl02.parsed.nml
expected: 1:12 error:
found   : (() ((f ((x ([] ([] bool 2)))) () ())))

---
# xic-ref (--parse [basic-error test]): paramdecl02.xi
Mismatch detected at line 1 of file paramdecl02.parsed.nml
expected: 1:12 error:
found   : (() ((f ((x ([] ([] bool 2)))) () ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:bool[][2]) {}

```

## Generated result for --parse:

```
(() ((f ((x ([] ([] bool 2)))) () ())))

```

## Expected result for --parse:

```
1:12 error:Syntax error: unexpected integer 2.

```

---
 paramdecl03.xi: Mismatch detected at line 1 of file paramdecl03.parsed.nml
expected: 1:11 error:
found   : (() ((f ((x ([] ([] ([] int) 3)))) () ())))

---
# xic-ref (--parse [basic-error test]): paramdecl03.xi
Mismatch detected at line 1 of file paramdecl03.parsed.nml
expected: 1:11 error:
found   : (() ((f ((x ([] ([] ([] int) 3)))) () ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
f(x:int[][3][]) {}

```

## Generated result for --parse:

```
(() ((f ((x ([] ([] ([] int) 3)))) () ())))

```

## Expected result for --parse:

```
1:11 error:Syntax error: unexpected integer 3.

```

---
 paramdecl04.xi: OK
 use01.xi: OK
 use02.xi: OK
 vardecl01.xi: Mismatch detected at line 1 of file vardecl01.parsed.nml
expected: 2:12 error:
found   : (() ((f () () ((x ([] ([] bool 1)))))))

---
# xic-ref (--parse [basic-error test]): vardecl01.xi
Mismatch detected at line 1 of file vardecl01.parsed.nml
expected: 2:12 error:
found   : (() ((f () () ((x ([] ([] bool 1)))))))
## Command line without filenames:
xic --parse
## Content of test case:

```
f() {
  x:bool[][1]
}

```

## Generated result for --parse:

```
(() ((f () () ((x ([] ([] bool 1)))))))

```

## Expected result for --parse:

```
2:12 error:Syntax error: unexpected integer 1.

```

---
 vardecl02.xi: Mismatch detected at line 1 of file vardecl02.parsed.nml
expected: 2:12 error:
found   : (() ((f () () ((x ([] ([] ([] bool) 1)))))))

---
# xic-ref (--parse [basic-error test]): vardecl02.xi
Mismatch detected at line 1 of file vardecl02.parsed.nml
expected: 2:12 error:
found   : (() ((f () () ((x ([] ([] ([] bool) 1)))))))
## Command line without filenames:
xic --parse
## Content of test case:

```
f() {
  x:bool[][1][]
}

```

## Generated result for --parse:

```
(() ((f () () ((x ([] ([] ([] bool) 1)))))))

```

## Expected result for --parse:

```
2:12 error:Syntax error: unexpected integer 1.

```

---
 vardecl03.xi: OK
 vardecl04.xi: OK
 vardecl05.xi: OK
 vardecl06.xi: Mismatch detected at line 1 of file vardecl06.parsed.nml
expected: 2:11 error:
found   : 3:1 error:

---
# xic-ref (--parse [basic-error test]): vardecl06.xi
Mismatch detected at line 1 of file vardecl06.parsed.nml
expected: 2:11 error:
found   : 3:1 error:
## Command line without filenames:
xic --parse
## Content of test case:

```
f() {
  x:int[1], _
}

```

## Compiler's standard output:
Syntax Error
3:1 error:Unexpected token }

## Generated result for --parse:

```
3:1 error:Unexpected token }

```

## Expected result for --parse:

```
2:11 error:Syntax error: unexpected ,.

```

---
 vardecl07.xi: Mismatch detected at line 1 of file vardecl07.parsed.nml
expected: 2:11 error:
found   : (() ((f () () (((x ([] int 1)) (y ([] bool 2)))))))

---
# xic-ref (--parse [basic-error test]): vardecl07.xi
Mismatch detected at line 1 of file vardecl07.parsed.nml
expected: 2:11 error:
found   : (() ((f () () (((x ([] int 1)) (y ([] bool 2)))))))
## Command line without filenames:
xic --parse
## Content of test case:

```
f() {
  x:int[1], y:bool[2]
}

```

## Generated result for --parse:

```
(() ((f () () (((x ([] int 1)) (y ([] bool 2)))))))

```

## Expected result for --parse:

```
2:11 error:Syntax error: unexpected ,.

```

---
 vardecl08.xi: Mismatch detected at line 1 of file vardecl08.parsed.nml
expected: 2:12 error:
found   : (() ((f () () ((= (x ([] int 1)) (1))))))

---
# xic-ref (--parse [basic-error test]): vardecl08.xi
Mismatch detected at line 1 of file vardecl08.parsed.nml
expected: 2:12 error:
found   : (() ((f () () ((= (x ([] int 1)) (1))))))
## Command line without filenames:
xic --parse
## Content of test case:

```
f() {
  x:int[1] = {1}
}

```

## Generated result for --parse:

```
(() ((f () () ((= (x ([] int 1)) (1))))))

```

## Expected result for --parse:

```
2:12 error:Syntax error: unexpected =.

```

---
 xic-ref (--parse [basic-error test]): 37 out of 49 tests succeeded.
 Test collection: xic-ref (--parse [combo test])
 group_of_anonymous01_01.xi: Mismatch detected at line 54 of file group_of_anonymous01_01.parsed.nml
expected:     (= i (- 9223372036854775808))
found   :     (= i -9223372036854775808)

---
# xic-ref (--parse [combo test]): group_of_anonymous01_01.xi
Mismatch detected at line 54 of file group_of_anonymous01_01.parsed.nml
expected:     (= i (- 9223372036854775808))
found   :     (= i -9223372036854775808)
## Command line without filenames:
xic --parse
## Content of test case:

```
main() {
  i = -f()[f()[3]] * !f()[f()[3]] / 2 *>> 5 % f + 1 - f()[3] < 4 <= z >= k > 5 == 1 != 2 & 3 | 4;
  i = !(1 + 2)[1]
  i = 3 * - 4 * 5
  i = 'a'
  i = "asd"
  i = '\n'
  i = "string_with\nnewline"
  i = "asd" + "bsd"
  i = 'a' + 'b'
  i = {1, {2, 3, 4}, f()}
  i = {1, {2, 3, 4}, f(),}
  i = length(asd())
  i = length({1, 2, 3})
  i = length({1, 2, 3,})
  i = true
  i = false
  i = a
  i = f()
  i = f(1)
  i = f(1, 2)
  i = f[1][2][f(x)]
  i = -1
  i = -a
  i = 9223372036854775807
  i = -9223372036854775808
  i = 2 - - 5
}

```

## Generated result for --parse:

```
(()
 ((main
   ()
   ()
   ((=
     i
     (|
      (&
       (!=
        (==
         (>
          (>=
           (<=
            (<
             (-
              (+
               (%
                (*>> (/ (* (- ([] (f) ([] (f) 3))) (! ([] (f) ([] (f) 3)))) 2)
                 5)
                f)
               1)
              ([] (f) 3))
             4)
            z)
           k)
          5)
         1)
        2)
       3)
      4))
    (= i (! ([] (+ 1 2) 1)))
    (= i (* (* 3 (- 4)) 5))
    (= i 'a')
    (= i "asd")
    (= i '\n')
    (= i "string_with\nnewline")
    (= i (+ "asd" "bsd"))
    (= i (+ 'a' 'b'))
    (= i (1 (2 3 4) (f)))
    (= i (1 (2 3 4) (f)))
    (= i (length (asd)))
    (= i (length (1 2 3)))
    (= i (length (1 2 3)))
    (= i true)
    (= i false)
    (= i a)
    (= i (f))
    (= i (f 1))
    (= i (f 1 2))
    (= i ([] ([] ([] f 1) 2) (f x)))
    (= i (- 1))
    (= i (- a))
    (= i 9223372036854775807)
    (= i -9223372036854775808)
    (= i (- 2 (- 5)))))))

```

## Expected result for --parse:

```
(()
 ((main
   ()
   ()
   ((=
     i
     (|
      (&
       (!=
        (==
         (>
          (>=
           (<=
            (<
             (-
              (+
               (%
                (*>> (/ (* (- ([] (f) ([] (f) 3))) (! ([] (f) ([] (f) 3)))) 2)
                 5)
                f)
               1)
              ([] (f) 3))
             4)
            z)
           k)
          5)
         1)
        2)
       3)
      4))
    (= i (! ([] (+ 1 2) 1)))
    (= i (* (* 3 (- 4)) 5))
    (= i 'a')
    (= i "asd")
    (= i '\n')
    (= i "string_with\nnewline")
    (= i (+ "asd" "bsd"))
    (= i (+ 'a' 'b'))
    (= i (1 (2 3 4) (f)))
    (= i (1 (2 3 4) (f)))
    (= i (length (asd)))
    (= i (length (1 2 3)))
    (= i (length (1 2 3)))
    (= i true)
    (= i false)
    (= i a)
    (= i (f))
    (= i (f 1))
    (= i (f 1 2))
    (= i ([] ([] ([] f 1) 2) (f x)))
    (= i (- 1))
    (= i (- a))
    (= i 9223372036854775807)
    (= i (- 9223372036854775808))
    (= i (- 2 (- 5)))))))

```

---
 group_of_anonymous01_02.xi: Mismatch detected at line 1 of file group_of_anonymous01_02.parsed.nml
expected: (() ((main () () ((f) (if a ((return))) ((return)) ((f)) ((())) (return)))))
found   : (() ((main () () ((f) (if a ()) () ((f)) ((())) (return)))))

---
# xic-ref (--parse [combo test]): group_of_anonymous01_02.xi
Mismatch detected at line 1 of file group_of_anonymous01_02.parsed.nml
expected: (() ((main () () ((f) (if a ((return))) ((return)) ((f)) ((())) (return)))))
found   : (() ((main () () ((f) (if a ()) () ((f)) ((())) (return)))))
## Command line without filenames:
xic --parse
## Content of test case:

```
main() {
  f()
  if (a) {
    return;
  }
  {
    return;
  }
  {
    f()
  }
  {{{}}}
  return;
}

```

## Generated result for --parse:

```
(() ((main () () ((f) (if a ()) () ((f)) ((())) (return)))))

```

## Expected result for --parse:

```
(() ((main () () ((f) (if a ((return))) ((return)) ((f)) ((())) (return)))))

```

---
 group_of_anonymous01_03.xi: Mismatch detected at line 2 of file group_of_anonymous01_03.parsed.nml
expected:  ((gcd ((a int) (b int)) (int) ((if (> a b) ((return 1)) ((return 46235)))))
found   :  ((gcd ((a int) (b int)) (int) ((if (> a b) () ())))

---
# xic-ref (--parse [combo test]): group_of_anonymous01_03.xi
Mismatch detected at line 2 of file group_of_anonymous01_03.parsed.nml
expected:  ((gcd ((a int) (b int)) (int) ((if (> a b) ((return 1)) ((return 46235)))))
found   :  ((gcd ((a int) (b int)) (int) ((if (> a b) () ())))
## Command line without filenames:
xic --parse
## Content of test case:

```
gcd(a:int, b:int):int {
  if (a > b) {
    return(1);
  } else {
    return(46235);
  }
  // ...no return statement
}

main() {
  x:int['a']
  x:int[f(x)]
  x:int["hello"]
  foo(a, 1, {1,2})
  a = {1, a, b+c, {1, c}, foo(1) }
  {f();}
  {return;}
  {f()}
  {return}
}

```

## Generated result for --parse:

```
(()
 ((gcd ((a int) (b int)) (int) ((if (> a b) () ())))
  (main
   ()
   ()
   ((x
     ([] int 'a'))
    (x ([] int (f x)))
    (x ([] int "hello"))
    (foo a 1 (1 2))
    (= a (1 a (+ b c) (1 c) (foo 1)))
    ((f))
    ()
    ((f))
    ()))))

```

## Expected result for --parse:

```
(()
 ((gcd ((a int) (b int)) (int) ((if (> a b) ((return 1)) ((return 46235)))))
  (main
   ()
   ()
   ((x
     ([] int 'a'))
    (x ([] int (f x)))
    (x ([] int "hello"))
    (foo a 1 (1 2))
    (= a (1 a (+ b c) (1 c) (foo 1)))
    ((f))
    ((return))
    ((f))
    ((return))))))

```

---
 group_of_anonymous02_01.xi: OK
 group_of_anonymous03_01.xi: OK
 group_of_anonymous04_01.xi: Mismatch detected at line 1 of file group_of_anonymous04_01.parsed.nml
expected: (()
found   : 4:8 error:

---
# xic-ref (--parse [combo test]): group_of_anonymous04_01.xi
Mismatch detected at line 1 of file group_of_anonymous04_01.parsed.nml
expected: (()
found   : 4:8 error:
## Command line without filenames:
xic --parse
## Content of test case:

```
foo(a:int, b:int[][], c:int[]):int, bool[][], bool, int, bool {
    a:bool, foo:int, _, foobar:int[][][] = bar()
    a:int, b:int[][], c:bool[] = foo()
    _, _, _ = foobar()
    c:int[5][3]
    d:int[4][2][5][][][]
    e:int[]
    c[2][3] = 4
    d[1][3][4][6] = {1}
    return a, b, {1, 2, 3}, d
}


```

## Compiler's standard output:
Syntax Error
4:8 error:Unexpected token _

## Generated result for --parse:

```
4:8 error:Unexpected token _

```

## Expected result for --parse:

```
(()
 ((foo
   ((a int) (b ([] ([] int))) (c ([] int)))
   (int
    ([] ([] bool)) bool int bool)
   ((=
     ((a bool) (foo int) _ (foobar ([] ([] ([] int))))) (bar))
    (= ((a int) (b ([] ([] int))) (c ([] bool))) (foo))
    (= (_ _ _) (foobar))
    (c ([] ([] int 3) 5))
    (d ([] ([] ([] ([] ([] ([] int))) 5) 2) 4))
    (e ([] int))
    (= ([] ([] c 2) 3) 4)
    (= ([] ([] ([] ([] d 1) 3) 4) 6) (1))
    (return a b (1 2 3) d)))))

```

---
 group_of_anonymous04_02.xi: OK
 group_of_anonymous04_03.xi: OK
 group_of_anonymous05_01.xi: OK
 group_of_anonymous05_02.xi: OK
 group_of_anonymous06_01.xi: OK
 group_of_anonymous06_02.xi: Mismatch detected at line 1 of file group_of_anonymous06_02.parsed.nml
expected: (()
found   : (() ((foo ((foo int)) (bool) ((if (< foo 0) () ())))))

---
# xic-ref (--parse [combo test]): group_of_anonymous06_02.xi
Mismatch detected at line 1 of file group_of_anonymous06_02.parsed.nml
expected: (()
found   : (() ((foo ((foo int)) (bool) ((if (< foo 0) () ())))))
## Command line without filenames:
xic --parse
## Content of test case:

```

foo(foo:int) : bool {
    if (foo < 0) {
    return foo(foo(0)[0])[0]
    }
    else {
    return true;
    }
}

```

## Generated result for --parse:

```
(() ((foo ((foo int)) (bool) ((if (< foo 0) () ())))))

```

## Expected result for --parse:

```
(()
 ((foo ((foo int)) (bool)
   ((if (< foo 0) ((return ([] (foo ([] (foo 0) 0)) 0))) ((return true)))))))

```

---
 group_of_anonymous06_03.xi: OK
 group_of_anonymous06_04.xi: OK
 group_of_anonymous06_05.xi: OK
 group_of_anonymous07_01.xi: Mismatch detected at line 6 of file group_of_anonymous07_01.parsed.nml
expected:      ([]
found   :      ([] ([] ([] ([] a -9223372036854775808) (- 1 -9223372036854775808)) 2) 3)

---
# xic-ref (--parse [combo test]): group_of_anonymous07_01.xi
Mismatch detected at line 6 of file group_of_anonymous07_01.parsed.nml
expected:      ([]
found   :      ([] ([] ([] ([] a -9223372036854775808) (- 1 -9223372036854775808)) 2) 3)
## Command line without filenames:
xic --parse
## Content of test case:

```
main(a:int[][], b:int, c:int) : bool[] {
    a[-9223372036854775808][1--9223372036854775808][2][3] = k
    a = (-9223372036854775808) > -9223372036854775808 + --9223372036854775808
}

```

## Generated result for --parse:

```
(()
 ((main
   ((a ([] ([] int))) (b int) (c int))
   (([] bool))
   ((=
     ([] ([] ([] ([] a -9223372036854775808) (- 1 -9223372036854775808)) 2) 3)
     k)
    (=
     a
     (> -9223372036854775808
      (+ -9223372036854775808 (- -9223372036854775808))))))))

```

## Expected result for --parse:

```
(()
 ((main
   ((a ([] ([] int))) (b int) (c int))
   (([] bool))
   ((=
     ([]
      ([] ([] ([] a (- 9223372036854775808)) (- 1 (- 9223372036854775808))) 2)
      3)
     k)
    (=
     a
     (>
      (- 9223372036854775808)
      (+ (- 9223372036854775808) (- (- 9223372036854775808)))))))))

```

---
 group_of_anonymous07_02.xi: OK
 group_of_anonymous08_01.xi: OK
 group_of_anonymous08_02.xi: OK
 group_of_anonymous08_03.xi: Mismatch detected at line 1 of file group_of_anonymous08_03.parsed.nml
expected: (((use io) (use pigs))
found   : 47:26 error:

---
# xic-ref (--parse [combo test]): group_of_anonymous08_03.xi
Mismatch detected at line 1 of file group_of_anonymous08_03.parsed.nml
expected: (((use io) (use pigs))
found   : 47:26 error:
## Command line without filenames:
xic --parse
## Content of test case:

```
//This is a program that tests the whole shebang.

use io;
use pigs

return_multiple(): bool, int {
    
    return 1, 2
    
}

return_an_array(): int[][] {
    return {{1, 3}, {2, 4}, {3, 7}};
}

multiple_args(x:int, y:int[][][], z:bool) {

    print("I don't actually do anything!")

}

main(args: int[][]) {

    x:int = 5
    y:bool = false | (true & true);
    
    if (x < 12)
        if (y)
            print("Hello, worl\x64!")
        else if (!y)
            print("Goodbye, cruel world!")
        else
            print("How did I get here?")
    
    while (y) {
        x = x + '1';
        y = x == 12
    }
    
    {
        print("I'm inside a scope!")
        
        z:int[][] = {{2}, {1,}}
        
        q:int[1][z[0][1]][]
        
        return_an_array()[0] = return_an_array()[1];
        
    } 
    
    z:int, _ = return_multiple()
    
    an_array:bool[7]
    
    print(length((an_array)))
    
    x = length(return_an_array()[0])

}

```

## Compiler's standard output:
Syntax Error
47:26 error:Unexpected token [

## Generated result for --parse:

```
47:26 error:Unexpected token [

```

## Expected result for --parse:

```
(((use io) (use pigs))
 ((return_multiple () (bool int) ((return 1 2)))
  (return_an_array () (([] ([] int))) ((return ((1 3) (2 4) (3 7)))))
  (multiple_args ((x int) (y ([] ([] ([] int)))) (z bool)) ()
   ((print "I don\'t actually do anything!")))
  (main
   ((args ([] ([] int))))
   ()
   ((=
     (x int) 5)
    (= (y bool) (| false (& true true)))
    (if
     (< x 12)
     (if y
      (print "Hello, world!")
      (if (! y) (print "Goodbye, cruel world!") (print "How did I get here?"))))
    (while y ((= x (+ x '1')) (= y (== x 12))))
    ((print "I\'m inside a scope!") (= (z ([] ([] int))) ((2) (1)))
     (q ([] ([] ([] int) ([] ([] z 0) 1)) 1))
     (= ([] (return_an_array) 0) ([] (return_an_array) 1)))
    (= ((z int) _) (return_multiple))
    (an_array ([] bool 7))
    (print (length an_array))
    (= x (length ([] (return_an_array) 0)))))))

```

---
 group_of_anonymous09_01.xi: OK
 group_of_anonymous09_02.xi: OK
 xic-ref (--parse [combo test]): 15 out of 22 tests succeeded.
 Test collection: xic-ref (--parse [combo-error test])
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
 group_of_anonymous01_17.xi: Mismatch detected at line 1 of file group_of_anonymous01_17.parsed.nml
expected: 1:14 error:
found   : 1:16 error:

---
# xic-ref (--parse [combo-error test]): group_of_anonymous01_17.xi
Mismatch detected at line 1 of file group_of_anonymous01_17.parsed.nml
expected: 1:14 error:
found   : 1:16 error:
## Command line without filenames:
xic --parse
## Content of test case:

```
main() : int a {}

```

## Compiler's standard output:
Syntax Error
1:16 error:Unexpected token {

## Generated result for --parse:

```
1:16 error:Unexpected token {

```

## Expected result for --parse:

```
1:14 error:Syntax error: unexpected id a.

```

---
 group_of_anonymous01_18.xi: OK
 group_of_anonymous01_19.xi: OK
 group_of_anonymous03_01.xi: OK
 group_of_anonymous10_01.xi: OK
 group_of_anonymous10_02.xi: OK
 group_of_anonymous04_01.xi: OK
 group_of_anonymous06_01.xi: OK
 group_of_anonymous07_01.xi: OK
 group_of_anonymous07_02.xi: OK
 group_of_anonymous07_03.xi: OK
 group_of_anonymous07_04.xi: OK
 group_of_anonymous07_05.xi: OK
 group_of_anonymous07_06.xi: OK
 group_of_anonymous07_07.xi: OK
 group_of_anonymous08_01.xi: Mismatch detected at line 1 of file group_of_anonymous08_01.parsed.nml
expected: 2:14 error:
found   : (() ((fail_on_bad_decl () () ((= (x ([] int 3)) (1 2 3))))))

---
# xic-ref (--parse [combo-error test]): group_of_anonymous08_01.xi
Mismatch detected at line 1 of file group_of_anonymous08_01.parsed.nml
expected: 2:14 error:
found   : (() ((fail_on_bad_decl () () ((= (x ([] int 3)) (1 2 3))))))
## Command line without filenames:
xic --parse
## Content of test case:

```
fail_on_bad_decl() {
    x:int[3] = {1, 2, 3} // Will fail because can't initialize indexed array
}

```

## Generated result for --parse:

```
(() ((fail_on_bad_decl () () ((= (x ([] int 3)) (1 2 3))))))

```

## Expected result for --parse:

```
2:14 error:Syntax error: unexpected =.

```

---
 group_of_anonymous11_01.xi: OK
 group_of_anonymous11_02.xi: OK
 group_of_anonymous09_01.xi: OK
 group_of_anonymous12_01.xi: OK
 group_of_anonymous12_02.xi: OK
 group_of_anonymous12_03.xi: OK
 group_of_anonymous13_01.xi: OK
 group_of_anonymous13_02.xi: OK
 group_of_anonymous13_03.xi: OK
 group_of_anonymous13_04.xi: OK
 xic-ref (--parse [combo-error test]): 40 out of 42 tests succeeded.
 Test collection: xic-ref (--parse [extension test (might fail)])
 group_of_anonymous04_01.xi: OK
 xic-ref (--parse [extension test (might fail)]): 1 out of 1 tests succeeded.
 Test collection: xic-ref (--parse [extension-error test (might succeed)])
 group_of_anonymous01_01.xi: OK
 vardecl01.xi: OK
 xic-ref (--parse [extension-error test (might succeed)]): 2 out of 2 tests succeeded.
 Test collection: xic-ref (--parse [ixi])
 conv.ixi: OK
 i01.ixi: OK
 i02.ixi: OK
 i03.ixi: OK
 i04.ixi: OK
 i05.ixi: OK
 xic-ref (--parse [ixi]): 6 out of 6 tests succeeded.
 Test collection: xic-ref (--parse [ixi-error])
 x01.ixi: OK
 x02.ixi: OK
 x03.ixi: Mismatch detected at line 1 of file x03.parsed.nml
expected: 1:1 error:
found   : 1:41 error:

---
# xic-ref (--parse [ixi-error]): x03.ixi
Mismatch detected at line 1 of file x03.parsed.nml
expected: 1:1 error:
found   : 1:41 error:
## Command line without filenames:
xic --parse
## Content of test case:

```
use io // ixi cannot have use statements

```

## Compiler's standard output:
Syntax Error
1:41 error:Unexpected token EOF

## Generated result for --parse:

```
1:41 error:Unexpected token EOF

```

## Expected result for --parse:

```
1:1 error:Syntax error: unexpected use.

```

---
 x04.ixi: OK
 x05.ixi: Mismatch detected at line 1 of file x05.parsed.nml
expected: 2:7 error:
found   : (() ((bar () () ((= (b int) 42)))))

---
# xic-ref (--parse [ixi-error]): x05.ixi
Mismatch detected at line 1 of file x05.parsed.nml
expected: 2:7 error:
found   : (() ((bar () () ((= (b int) 42)))))
## Command line without filenames:
xic --parse
## Content of test case:

```
// ixi cannot have function bodies
bar() {
    b: int = 42;
}

```

## Generated result for --parse:

```
(() ((bar () () ((= (b int) 42)))))

```

## Expected result for --parse:

```
2:7 error:Syntax error: unexpected {.

```

---
 ex1_xi_as_ixi.ixi: Mismatch detected at line 1 of file ex1_xi_as_ixi.parsed.nml
expected: 1:1 error:
found   : (((use io))

---
# xic-ref (--parse [ixi-error]): ex1_xi_as_ixi.ixi
Mismatch detected at line 1 of file ex1_xi_as_ixi.parsed.nml
expected: 1:1 error:
found   : (((use io))
## Command line without filenames:
xic --parse
## Content of test case:

```
use io

main(args: int[][]) {
  print("Hello, Worl\x64!\n")
  c3po: int = 'x' + 47;
  r2d2: int = c3po // No Han Solo
}

```

## Generated result for --parse:

```
(((use io))
 ((main ((args ([] ([] int)))) ()
   ((print "Hello, World!\n") (= (c3po int) (+ 'x' 47)) (= (r2d2 int) c3po)))))

```

## Expected result for --parse:

```
1:1 error:Syntax error: unexpected use.

```

---
 ex2_xi_as_ixi.ixi: Mismatch detected at line 1 of file ex2_xi_as_ixi.parsed.nml
expected: 1:18 error:
found   : (()

---
# xic-ref (--parse [ixi-error]): ex2_xi_as_ixi.ixi
Mismatch detected at line 1 of file ex2_xi_as_ixi.parsed.nml
expected: 1:18 error:
found   : (()
## Command line without filenames:
xic --parse
## Content of test case:

```
foo(): bool, int {
  expr: int = 1 - 2 * 3 * -4 *
  5pred: bool = true & true | false;
  if (expr <= 47) { }
  else pred = !pred
  if (pred) { expr = 59 }
  return pred, expr;
}

bar() {
  _, i: int = foo()
  b: int[i][]
  b[0] = {1, 0}
}

```

## Generated result for --parse:

```
(()
 ((foo
   ()
   (bool int)
   ((=
     (expr int) (- 1 (* (* (* 2 3) (- 4)) 5)))
    (= (pred bool) (| (& true true) false))
    (if (<= expr 47) () (= pred (! pred)))
    (if pred ((= expr 59)))
    (return pred expr)))
  (bar () () ((= (_ (i int)) (foo)) (b ([] ([] int) i)) (= ([] b 0) (1 0))))))

```

## Expected result for --parse:

```
1:18 error:Syntax error: unexpected {.

```

---
 xic-ref (--parse [ixi-error]): 3 out of 7 tests succeeded.
 xthScript: 146 out of 193 tests succeeded.
