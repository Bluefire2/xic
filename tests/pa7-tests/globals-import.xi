use point
use io
use conv

x:Point;
y:Point = new Point;
z:Point[] = {new Point}

main(args:int[][]) {
    y = y.initPoint(0, 1)
    z[0] = z[0].initPoint(1, 2)
    yx: int, yy: int = y.coords()
    println("y = (" + unparseInt(yx) + "," + unparseInt(yy) + "yy" + ")")
}
