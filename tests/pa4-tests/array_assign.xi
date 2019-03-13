use io
use conv

main(args:int[][]) {
    //assigning to indexes
    x:int[] = "jello"
    println(x)
    x[0] = "h"[0]
    println(x)
    y:int[] = {1,2,3}
    y[0] = 0
    y[1] = y[2]
    println(unparseInt(y[0]))
    println(unparseInt(y[1]))
    //assigning to multidim array
    o:int[][] = {"jello","hello"}
    o[0][0] = o[1][0]
    o[1] = "world!"
    println(o[0]+ " "+o[1])
}