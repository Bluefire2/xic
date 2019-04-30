use io
use conv

main(args: int[][]) {
  x: int[] = {1, 2, 3}
  println(unparseInt(f(x, 1) + f(x, 2)))
  println(unparseInt(f(x, 3) - f(x, 2)))
  println(unparseInt(f(x, 4) * f(x, 2)))
  println(unparseInt(f(x, 5) *>> f(x, 2)))
  println(unparseInt(f(x, 6) / f(x, 2)))
  println(unparseInt(f(x, 7) % f(x, 2)))
  println(b2s(f(x, 1) < f(x, 2)))
  println(b2s(f(x, 3) <= f(x, 2)))
  println(b2s(f(x, 4) > f(x, 2)))
  println(b2s(f(x, 5) >= f(x, 2)))
  println(b2s(f(x, 6) == f(x, 2)))
  println(b2s(f(x, 7) != f(x, 2)))
}

f(x: int[], y: int): int {
  print("f() called: ")
  println(unparseInt(x[0]))
  println(unparseInt(y))
  x[0] = x[0] + 1
  return x[0]
}

b2s(b: bool): int[] {
  if (b) { return "true" } else { return "false" }
}
