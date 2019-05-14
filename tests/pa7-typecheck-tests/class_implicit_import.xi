// Implicitly imports interface. Interface imports b.ixi and so class C should
// type check since b has class B but implicit interface doesn't

class C extends B {
    c: int
}

asdf() {
    fb()// from b.ixi again

    // b.ixi imports a.ixi
    fa()
    aobj: A = new A
    aobj.ma()
}
