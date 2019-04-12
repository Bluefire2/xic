use io
use conv

main(args:int[][]) {
  a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int = ff();
  println({a,b,c,d,e,f,g,h})
}

ff(): int, int, int, int, int, int, int, int {
  return 'a','b','c','d','e','f','g','h';
}