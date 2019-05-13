use io
use conv

main(args: int[][]) {
  //define constants
  a : int = 2*(3233+(2225-52362)/7723*832-1322+28238%9)
  b : int = 12*(3232+(9320-22322)/7232*82333-12233+2%2)
  x : int = a
  y : int = b
  x1 : int = x + y
  x2 : int = x1 + y
  x3 : int = x + y
  x4 : int = x2 + x3
  x5 : int = x + y
  x6 : int = x4 + x5

  //redefine constants
  u : int = x
  w : int = y
  x7 : int = u + w
  x8 : int = x7 + u
  x9 : int = u + w
  x10 : int = x8 + x9
  x11 : int = u + w
  x12 : int = x10 + x11
  println("x1 is : " + unparseInt(x1))
  println("x7 is : " + unparseInt(x7))
  println("Survived Common Sub-Expression Elimination")
  println("... and possibly copy propagation and dead code removal")
}
