foo() : int {
    return bar() + 3
}

bar() : int {
    return foo() + 2
}