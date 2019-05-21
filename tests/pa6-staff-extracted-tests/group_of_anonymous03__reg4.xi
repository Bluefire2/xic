use io
use conv

f() {
  i: int = 0; a: int = 0; b: int = 1; c: int = 1
  while (i < 50) {
    a = b
    b = c
    c = a + b
    i = i + 1
  }
}

main(args: int[][]) {
  x: int = 0
  while (x < 2000000) {
    f()
    x = x + 1
  }
}
