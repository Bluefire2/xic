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
