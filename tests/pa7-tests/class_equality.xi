use conv
use io

class S {
    s: int
    initS(s0: int): S {
        s = s0
        return this
    }

    equals(s2:S): bool {
       return this == s2
    }
}


main(args:int[][]) {
        s1:S = new S.initS(6)
        s2:S = new S.initS(6)
        if (s1.equals(s1)) {
           println("object equals itself")
        }
        if (s1.equals(s2)) {
           println("object should not equal another object")
        }
}
