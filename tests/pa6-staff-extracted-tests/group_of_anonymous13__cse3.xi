use io
use conv

// cse should catch (a+b) as common subexpression
main(args:int[][]) {
  i:int = 0;
  while (i < 30000000) {
    x:int = foo();
    i = i + 1;
  }
  print("should be 10883911710, is ");
  print(unparseInt(foo()));
}

foo():int {
  a:int=5;b:int=6;
  c:int = (a+b*b*b*b*b*b*b*a*b*b*b*b)*2;
  d:int = (a+b*b*b*b*b*b*b*a*b*b*b*b)*4;
  e:int = (a+b*b*b*b*b*b*b*a*b*b*b*b)*5;
  f:int = (a+b*b*b*b*b*b*b*a*b*b*b*b)*6;
  return f;
}
