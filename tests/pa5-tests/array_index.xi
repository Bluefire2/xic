use io
use conv

main(args:int[][]) {
    //indexing
    x:int = {1,2,3,4}[2]
    println(unparseInt(x + x + {1,2,3,4}[2]))
    //indexing a string and building it back again
    h:int = "hello"[0]
    e:int = "hello"[1]
    l1:int = "hello"[2]
    l2:int = "hello"[3]
    o:int = "hello"[4]
    hello:int[] = {h,e,l1,l2,o}
    println(hello)
    //indexing with a non-constant
    y:int[] = {1}
    z:int[] = {1,2,3}
    println(unparseInt(z[y[0]]))
    println(unparseInt(z[1 + 1]))
}