class A {
    x: int
    f(): int { return 1 }
}
class B extends A {
    g(): int { return 2 }
}
class C extends B {
    g(): int { return 3 }
}
class D extends C {
    y: int
    f(): int { return 4 }
}
