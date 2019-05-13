use io
use conv

main(args:int[][]) {
  x:int = 0
  i:int = 0
  b:int = 4
  c:int = 7
  d:int = 99
  e:int = 42
  f:int = 37
  g:int = 89
  h:int = 88
  j:int = 17
  while (i < 50000000) {
    x = x + i * (b * c + d + e * f + g * h * j)
    i = i + 1
  }
  println(unparseInt(x))
}
