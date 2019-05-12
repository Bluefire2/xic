class A {
}
class B extends A {
}
class C extends A {
}
class D extends C {
}

f() {
    a: A
    b: B
    c: C
    d: D
    takesA(a)
    takesA(b)
    takesA(c)
    takesA(d)

    a = retA()
    a = retB()
    b = retB()
    c = retC()
    d_: D = retC()// error here: C not a subtype of D
    c = d_
    a = d_
    a = c
    a_: A, a__: A = retAB()
}

takesA(a_param: A) {}

retB(): B { return new B }

retC(): C { return new C }

retA(): A { return new A }

retAB(): A, B { return new A, new B }
