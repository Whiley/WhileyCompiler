import whiley.lang.*

type R1 is {real x}

type R2 is {int x}

function f(R2 i) -> R1:
    return (R1) i

method main(System.Console sys) -> void:
    sys.out.println(f({x: 123542}))
