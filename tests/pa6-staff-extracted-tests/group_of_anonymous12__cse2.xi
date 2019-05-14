//Speed test, with correctness of killing expressions
main(args:int[][]){
  w:int = 2
  x:int = 3
  y:int = 3
  z:int = 5
  i:int = 0
  a:int = w*x*y*z+w+x+y+z*w*x*y*z
  while(i < 100000000) {
    i = i+1
    a = w*x*y*z+w+x+y+z*w*x*y*z
  }
  y = 5
  a = w*x*y*z+w+x+y+z*w*x*y*z
  y = 4
  a = w*x*y*z+w+x+y+z*w*x*y*z
  if (a != 729) {a = 1/0}
}