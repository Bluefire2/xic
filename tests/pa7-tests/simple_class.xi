use io
use conv

class A {
    a: int
    initA(a0: int): A {
        a = a0
        return this
    }
    updateA(a0: int) {
        a = a0
    }
}

main(args:int[][]) {
    aobj: A = new A.initA(0)
    aobj.updateA(1)
    println(unparseInt(aobj.a))
}
