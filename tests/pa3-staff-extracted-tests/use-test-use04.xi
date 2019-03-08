use i01
use i03

foo() {
  p1()
  p2(true)
  p3(0, {1})
  x01: int = f1()
  x02: int = f2(true)
  x03: int = f3(0, {1})
  x04: int, b04: bool = f4()
  x05: int, b05: bool = f5(true)
  x06: int, b06: bool = f6(0, {1})
  x07: bool = g1()
  x08: bool = g2(0)
}

