f1(): bool, bool, bool, bool {
  return true, false, true & false, false | true
}

f2(): int, int, int, int {
  x: int
  return 0, x, 1-x, x*2;
}

f3(): int[], int[][], int[][][] {
  return {0}, {{0}}, {{{0}}}
}
