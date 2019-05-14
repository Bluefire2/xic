use io
use conv

main(args: int[][]) {
  n:int = 8000000; i:int = 0; x:int; y:int; z:int; w:int; a:int; b:int; c:int
  while(i < n){
    x = i; x = i + 1; z = 2; x = z * x; z = z/2; x = x+z; z = z-1; x=z;
    x = i; x = i + 1; z = 2; x = z * x; z = z/2; x = x+z; z = z-1; x=z;
    x = i; x = i + 1; z = 2; x = z * x; z = z/2; x = x+z; z = z-1; x=z;
    x = i; x = i + 1; z = 2; x = z * x; z = z/2; x = x+z; z = z-1; x=z;
    x = i; x = i + 1; z = 2; x = z * x; z = z/2; x = x+z; z = z-1; x=z;
    x = i; x = i + 1; z = 2; x = z * x; z = z/2; x = x+z; z = z-1; x=z;
    x = i; x = i + 1; z = 2; x = z * x; z = z/2; x = x+z; z = z-1; x=z;
    x = i; x = i + 1; z = 2; x = z * x; z = z/2; x = x+z; z = z-1; x=z;
    x = i; x = i + 1; z = 2; x = z * x; z = z/2; x = x+z; z = z-1; x=z;
    x = i; x = x; z = y; w = z; a = w; b = a; c = b; x = c; i = i + 1;
  }
}