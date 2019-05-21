use b// imports interface a
class C extends B {
    c: int
}

class D extends A {
    d: int
}

// Overriding b's declaration
fb() {}

main(args:int[][]) {
    fa()// should've been imported by b
    fb()
    c: C = new C
    c.ma()
    d: D = new D
    d.ma()
    a: A = new A
    a.ma()
}
