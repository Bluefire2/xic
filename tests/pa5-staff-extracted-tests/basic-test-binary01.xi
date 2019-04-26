use io
use conv

main(args: int[][]) {
  println(unparseInt(1 + 2))
  println(unparseInt(3 - 2))
  println(unparseInt(4 * 2))
  println(unparseInt(5 *>> 2))
  println(unparseInt(6 / 2))
  println(unparseInt(7 % 2))
}
