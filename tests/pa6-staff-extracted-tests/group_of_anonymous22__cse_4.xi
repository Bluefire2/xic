use io
use conv

main(args:int[][]) {
  a:int = 1
  b:int = 2
  c:bool = b > 5
  d:int = a + b + 1
  f:int = 0
  e:int = f + 1
  g:int = 1
  h:bool
  m:int
  while(f < 10){
    g = g * b
    h = b > 5
    m = a + b + 1
    f = f + 1
  }
  n:int = a + b + 1
  print(unparseInt(g))
}
