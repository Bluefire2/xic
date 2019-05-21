use io
use conv

main(args:int[][]) {
  i:int = 0
  j:int = 0
  x:int = 333949944
  y:int = 804800404433
  z:int = 784874
  a:int = y + ((x * y)/z)*x
  b:int = 0
  while (i < 1000000) {
    b = ((x * y)/z)*x + (y * z)
    c:int = 0
    if (j < 10) {
      c = c + y * z
      j = j + 1
    }
    i = i + 1
  }
  println(unparseInt(b))
}
