import * from whiley.lang.*

type R1 is {int y, real x}

type R2 is {real y, int x}

type R3 is {int y, int x}

function f(R1 | R2 i) -> R3:
    return (R3) i

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f({y: 123, x: 123542.0})))
