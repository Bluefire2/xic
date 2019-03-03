bar() {
    x:int, a:int[][], _ = foo()
}

foo() : int, bool, int[][], bool[] {
    return 1, true, {{1, 0}, {2}}, {1 == 1};
}