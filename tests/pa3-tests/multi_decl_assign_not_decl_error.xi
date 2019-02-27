bar() {
    x:int
    y:bool
    x, y = foo()
    return
}

foo() : int, bool {
    return 1+1, 'a' == "abc"[0]
}