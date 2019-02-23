f(x:int, y:bool, z:int[]) {
    x = 0
    y = true
    x = x
    z = {1, 2, 3}
    x = g1()
    x = z[0]
    x = -1
    x = 0 + 0
    x = (0)
}

g1():int {
    return 0
}


// Test to correctly print function return statements
