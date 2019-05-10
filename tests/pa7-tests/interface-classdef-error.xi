use point

class ColoredPoint extends Point {
  col: Color
  color(): Color { return col }

  initColoredPoint(x0: int, y0: int, c: Color): ColoredPoint {
      col = c
      _ = initPoint(x0, y0)
      return this
  }
}