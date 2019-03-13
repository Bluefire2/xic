use io

main(args:int[][]) {
    a: int[][]
    b: int[1][]
    // testing array copying
    b[0] = {'a', 'b'}

    print(b[0])
    print(b[0] + "\n")

    // testing array copying
    c: int[3][4]
    a = c
    d: int[3][]
    d[0] = c[0]; d[1] = c[1]; d[2] = c[2]

    // testing mutli array dim allocation and copying
    e: int[][] = {{'a', 'b'}, {'c', 'd'}}
    i: int = 0
    while (i < length(e)) {
        print(e[i] + "\n")
        i = i + 1
    }

    f: int[][] = {"aaaa", "bbbb", "cccc"}
    c = f;
    g: int[3][]
    g[0] = f[0]; g[1] = f[1]; g[2] = f[2]
    println(g[0]+ {g[0][0]} + g[1] + {g[1][0]} + g[2] + {g[2][0]})
}
