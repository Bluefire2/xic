use io
use conv

main(args:int[][]) {
    x:int[][] = {{1,2},{3,4}}
    //indexing tests
    println(unparseInt(x[0][0]))
    println(unparseInt(x[0][1]))
    println(unparseInt(x[1][0]))
    println(unparseInt(x[1][1]))
    //concating literals then indexing
    println(unparseInt((x + x)[2][1]))
    println(unparseInt((x + x)[3][1]))
}