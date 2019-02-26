use io
exp(a: int, b: int): int {
    if (b == 0) {
        return 1
    } else if (b == 1) {
        return a
    } else {
        return a * exp(a, b - 1)
    }
}