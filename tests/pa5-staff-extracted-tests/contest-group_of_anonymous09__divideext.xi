use io use conv
// Make sure people sign extend!  
one() : int {
    x:int[1] return length(x)
} 
neg_one() : int {
    x:int[1] return -length(x)
} 
fill_rdx_with_ones() : int, int {
    x:int[1] return length(x), -1
}
main(args:int[][]) {
    b:int = 10;
    print("Negative ten: ");
    println(unparseInt(b / neg_one()));
    unity:int = one();
    _, _ = fill_rdx_with_ones();
    print("Positive ten: ");
    println(unparseInt(b / unity));
}
