f() {
  x01:int = 0
  x02:bool = true
  x03:int = x01
  x04:int[] = {1, 2, 3}
  x05:int = g1()
  x06:int = x04[0]
  x07:int = -1
  x08:int = 0 + 0
  x09:int = (0)
  x10:int[][] = {{1}, {2, 3}}
}

g1():int {
  return 0
}

