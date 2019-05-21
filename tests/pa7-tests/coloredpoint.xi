use io
use conv
use point

class Color {
    r, g, b: int
}

class ColoredPoint extends Point {
    col: Color
    color(): Color { return col }

    initColoredPoint(x0: int, y0: int, c: Color): ColoredPoint {
        col = c
        _ = initPoint(x0, y0)
        return this
    }
}

main(args:int[][]) {
    c:Color = new Color
    c.r = 1; c.g = 2; c.b = 3;

    p:Point = new ColoredPoint
    _ = p.initPoint(1, 2)
}
