use io
use conv

//this is a modified version of the sample code in the simulator example
//intended to test our translation

a(i:int, j:int): int, int {
    return i, (2 * j);
}

b(i:int, j:int): int {
    x:int, y:int = a(i, j);
    return x + 5 * y;
}

main(args:int[][]) {
    println("b(2,1) == " + unparseInt(b(2,1)));
}