use io
use conv

inc(x:int) : int{
    return x + 1
}

main(args:int[][]) {
    x:int = inc(1)
    print(unparseInt(x))
}
