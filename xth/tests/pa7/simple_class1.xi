use io
use conv

class S {
    s: int
    initS(s0: int): S {
        s = s0
        return this
    }
}

class A extends S {
    a: int
    initA(s0:int, a0: int): A {
        a = a0
        _ = initS(s0)
        return this
    }
    updateA(a0: int) {
        a = a0
    }
}

main(args:int[][]) {
    aobj: A = new A.initA(0, 1)
    aobj.updateA(2)
    println(unparseInt(aobj.s))
    println(unparseInt(aobj.a))
}
