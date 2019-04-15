use io
use conv

dbz(x:int):int {
    return x/0
}

main(args:int[][]) {
    //this should not crash in constant folding
    println("hello");
}