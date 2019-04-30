use io
use conv

main(args: int[][]) {
  println(unparseInt(f(1) + f(2)))
  println(unparseInt(f(3) - f(2)))
  println(unparseInt(f(4) * f(2)))
  println(unparseInt(f(5) *>> f(2)))
  println(unparseInt(f(6) / f(2)))
  println(unparseInt(f(7) % f(2)))
}

f(x: int): int {
  print("f() called: ")
  println(unparseInt(x))
  return x
}
