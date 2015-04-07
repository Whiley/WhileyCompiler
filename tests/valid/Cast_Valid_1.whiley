import whiley.lang.*

type R1 is {real x}

function f(int i) -> real:
    return (real) i

method main(System.Console sys) -> void:
    sys.out.println(f(1))
