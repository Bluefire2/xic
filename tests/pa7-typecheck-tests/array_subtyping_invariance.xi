class A {
}
class B extends A {
}
class C extends A {
}

f() {
    as: A[]
    bs: B[]
    cs: C[]
    takesAarr(bs)
}

takesAarr(as_param: A[]) {}
