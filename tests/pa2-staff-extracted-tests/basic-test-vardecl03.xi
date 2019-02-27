f() {
  _ = g1()
  _, _ = g2()
  _, x032:int = g2()
  x041:int, _ = g2()
  x051:int, x052:int = g2()
  _, _, _ = g3()
  _, _, x073:int = g3()
  _, x082:int, _ = g3()
  x091:int, _, _ = g3()
  x101:int, x102:int, _ = g3()
  x111:int, _, x113:int = g3()
  _, x122:int, x123:int = g3()
  x131:int, x132:int, x133:int = g3()
}

g1():int {
  return 0
}

g2():int, int {
  return 0, 0
}

g3():int, int {
  return 0, 0, 0
}

