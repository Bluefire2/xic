use io
use conv

main(args:int[][]) {
    i: int = 0
    j: int = 0
    while (i < 2) {
    while(j < 5) {
        if (j==0) {
            break
        }
    }
    println(unparseInt(i))
    i = i + 1
    }
    println(unparseInt(i))
}
