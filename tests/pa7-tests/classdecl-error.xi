use point

class Color {
  r,g,b: int
}

class ColoredPoint extends Point {
  col: Color
  color(): Color

  initColoredPoint(x0: int, y0: int, c: Color): ColoredPoint
}