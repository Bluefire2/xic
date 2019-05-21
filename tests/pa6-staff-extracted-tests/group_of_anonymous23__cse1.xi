use io
use conv

main(args:int[][]) {
  i:int = 0
  x:int = 333949944
  y:int = 804800404433
  z:int = 784874
  a:int = y + ((x * y)/z)*x
  b:int = 0
  while (i < 10000000) {
    b = x + ((x * y)/z)*x
    i = i + 1
  }
  println(unparseInt(b))
}
