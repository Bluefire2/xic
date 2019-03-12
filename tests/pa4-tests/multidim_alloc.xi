use io

main(args:int[][]) {
    a: int[][]
    b: int[1][]
    b[0] = {'a', 'b'}
    // b: int[3][4]
    // a = b
    // c: int[3][]
    // c[0] = b[0]; c[1] = b[1]; c[2] = b[2]
    // d: int[][] = {{'a', 'b'}, {'c', 'd'}}
    print(b[0])
}
