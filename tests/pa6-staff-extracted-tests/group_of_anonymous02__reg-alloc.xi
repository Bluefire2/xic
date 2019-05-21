use io
use conv

main(args:int[][]) {
  x:int = 0
  i:int = 0
  b:int = 4
  c:int = 7
  d:int = 99
  while (i < 50000000) {
    x = x + i * (b + c * b + d + b)
  i = i + 1
  }
  println(unparseInt(x))
}
