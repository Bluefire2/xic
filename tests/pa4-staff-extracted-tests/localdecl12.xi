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
