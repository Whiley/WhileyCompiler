import * from whiley.lang.*

type R1 is {real x}

function f(real i) -> int:
    return (int) i

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f(1.01)))
