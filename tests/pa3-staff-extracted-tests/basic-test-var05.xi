foo() {
  i00: int = f1()
  _ = f1()

  i01: int, b01: bool = f2();
  i02: int, _ = f2();
  _, b03: bool = f2();
  _, _ = f2();

  i05: int, b05: bool, ai05: int[] = f3();
  i06: int, b06: bool, _ = f3();
  i07: int, _, ai07: int[] = f3();
  i08: int, _, _ = f3();
  _, b09: bool, ai09: int[] = f3();
  _, b10: bool, _ = f3();
  _, _, ai11: int[] = f3();
  _, _, _ = f3();
}

f1(): int {
  return 0
}

f2(): int, bool {
  return 0, true
}

f3(): int, bool, int[] {
  return 0, true, {1}
}
