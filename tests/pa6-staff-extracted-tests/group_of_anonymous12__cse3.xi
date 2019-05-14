// Speed test and correctness
use io
use conv

main(args:int[][]) {
  x:int = 3
  y:int = x*x*x*x*x*x*x
  i:int = 0
  while (i < 100000000) {
    y = x*x*x*x*x*x*x
    i=i+1
  }
  x=1
  x=x*x*x*x*x*x*x+1
  y = x*x*x*x*x*x*x
  if (y != 128) {y=1/0}
}