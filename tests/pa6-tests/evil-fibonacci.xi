use conv
use io

evil_fib(n:int) : int {
    if (n <= 1) {
        return n
    } else {
        rsp : int[n + 1]
        rbp:int = 2
        rsp[0] = 0
        rsp[1] = 1
        while(rbp <= n) {
            rsp[rbp] = rsp[rbp - 1] + rsp[rbp - 2]
            rbp = rbp + 1
        }
        rax:int = rsp[n] * 1
        return rax
    }
}

main(args:int[][]) {
    //50th fibonacci number except all our variable names are register names
    rbp : int = 0
    rbp = evil_fib(50)
    println("should be: " + unparseInt(12586269025))
    println("we got: " + unparseInt(rbp))
}