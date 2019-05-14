use class_implements_interface

class A {
    a: int
    fa(p: int): bool { return true }
}

class B extends A {
    b: int
    fb(p: int): int { return 0 }
}

class LocalClass extends B {
    asdf:int
}

local_f() {}
