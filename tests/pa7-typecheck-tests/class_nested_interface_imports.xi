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
}
