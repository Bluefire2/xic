// fields initialized by other fields, methods etc.
class A {
    a: int[fa()]
    fa(): int { return 1 }
}

class B extends A {
    len: int
    b: int[fb()][fa()][]
    b_: int[len][b[1][0][2]]
    b__: int[fb_()[0]]
    fb(): int { return 2 }
    fb_(): int[] { return {2, 3, 4} }
}

class C extends B {
    c, c_: int[len]
    c__: int[fb_()[1]]
    c___: int[a[1]]
}
