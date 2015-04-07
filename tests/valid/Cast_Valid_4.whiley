import whiley.lang.*

type R1 is {int y, real x}

type R2 is {real y, int x}

type R3 is {int y, int x}

function f(R3 i) -> R1 | R2:
    return (R1) i

method main(System.Console sys) -> void:
    sys.out.println(f({y: 123, x: 123542}))
