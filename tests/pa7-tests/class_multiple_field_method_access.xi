use io
use conv

class A {
    a: int
    fa(): int { return 'a' }
    initA(a_: int): A {
        a = a_
        return this
    }
}

class C {
    c: int
    initC(c_: int): C {
        c = c_
        return this
    }
}

class B extends A {
    afield: A
    b: int
    cs: C[3]
    fb(): int { return 'b' }
    initB(a_: int, afield_: A, b_: int): B {
        _ = initA(a_)
        afield = afield_
        b = b_

        i: int = 0
        while (i < 3) {
            cs[i] = new C.initC(i)
            i = i + 1
        }
        return this
    }
    get_afield(): A {
        return afield
    }
}

main(args:int[][]) {
    aobj_inB: A = new A.initA(1)
    bobj: B = new B.initB(0, aobj_inB, 2)

    // access method of an obj returned by a method
    println(unparseInt(bobj.get_afield().fa()))

    // access field of an obj returned by a method
    bobj.get_afield().a = 11
    println(unparseInt(bobj.get_afield().a))
    println(unparseInt(aobj_inB.a))

    // access field of an obj that is a field
    bobj.afield.a = 22
    println(unparseInt(bobj.get_afield().a))
    println(unparseInt(aobj_inB.a))

    // access method of an object
    x:int = bobj.fb()

    // access method defined by a super class of an object
    x = bobj.fa()

    // access field of object arrays
    i: int = 0
    while (i < length(bobj.cs)) {
        println(unparseInt(bobj.cs[i].c))
        i = i + 1
    }
}
