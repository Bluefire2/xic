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
