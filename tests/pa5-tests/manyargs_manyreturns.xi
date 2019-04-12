use io
use conv

main(args:int[][]) {
  a:int, b:int, c:int, d:int = ff('a','b','c','d','e','f','g','h');
  println({a,b,c,d});
}

ff(a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int):int,int,int,int {
  return a, b, c, d;
}