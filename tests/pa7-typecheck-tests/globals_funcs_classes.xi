use lang_spec_point_interface
use io
use conv

x:int = 5;
y:int[x];

class Color {
  r,g,b: int
}

main(args:int[][]){
    e:Point;
    f,g:Color;
    h:ColoredPoint = new ColoredPoint;
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

f1(){
    e:Point;
    f,g:Color;
    h:ColoredPoint = new ColoredPoint;
}

z:int[] = "hello";
aa,bb,cc:int;


