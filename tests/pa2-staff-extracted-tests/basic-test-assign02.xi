f(x1:int[], x2:int[][]) {
  x1[0] = 0
  x2[0][1] = 0
  g1()[0] = 0
  g2()[0][1] = 0
}

g1():int[] {
  return {0}
}

g2():int[][] {
  return {{0}}
}

