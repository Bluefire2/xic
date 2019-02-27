foo(a:int, b:int) : int, int, int, int {
    x:int, y:int, w:int, z:int = foo(bar())
    return bar(), bar()

}

bar() : int, int {
    return 3, 4
}