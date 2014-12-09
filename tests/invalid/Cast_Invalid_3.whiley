import * from whiley.lang.*

type R2 is {real x}

type R1 is {int x}

function f(R2 i) -> R1:
    return (R1) i

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f({x: 123542.12})))
