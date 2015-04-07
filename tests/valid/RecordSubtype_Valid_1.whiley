import whiley.lang.*

type R1 is {null | int x}

type R2 is {int x}

type R3 is {null x}

type R4 is R2 | R3

function f(R1 x) -> R4:
    return x

method main(System.Console sys) -> void:
    z1 = f({x: 1})
    z2 = f({x: null})
    sys.out.println(z1)
    sys.out.println(z2)
