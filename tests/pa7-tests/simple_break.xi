use io
use conv

main(args:int[][]) {
    i: int = 0
    while (i < 10) {
        if (i == 5) {
            break
        }
        println(unparseInt(i))
        i = i + 1
    }
    println(unparseInt(i))
}
