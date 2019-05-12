class Point{ // a mutable point
    x, y: int

    move(dx: int, dy: int) {
        x = x + dx
        y = y + dy
    }
    coords(): int, int {
        return x, y
    }
    add(p: Point): Point {
        return createPoint(x + p.x, y + p.y)
    }
    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        return this
    }
    clone(): Point { return createPoint(x, y) }
    equals(p: Point): bool { return this == p }
}

createPoint(x: int, y:int): Point {
    return new Point.initPoint(x, y)
}

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

center: Point
corner: Point
len: int = 10
tenpoints: Point[len]
