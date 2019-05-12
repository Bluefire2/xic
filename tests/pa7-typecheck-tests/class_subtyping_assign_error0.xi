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
    b = a// error here: B not a subtype of A
    d_: D = retD()
    c = d
    a = d
    a = c
    a_: A, a__: A = retAB()
}

takesA(a_param: A) {}

retB(): B { return new B }

retD(): D { return new D }

retA(): A { return new A }

retAB(): A, B { return new A, new B }
