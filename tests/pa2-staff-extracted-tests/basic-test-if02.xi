f() {}

f1() {
  if (true) if (true) f()
  if (true) if (true) f() else f()
  if (true) while (true) f()
  if (true) f()
  if (true) {}
  if (true) { return }
  if (true) x:int[0]
}

f2(x:int, y:int) {
  if (true) x = y
}

