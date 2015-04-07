import whiley.lang.*

type pos is real

type neg is int

type expr is pos | neg | [int]

function f(expr e) -> int:
    if (e is pos) && (e > 0.0):
        return 0
    else:
        if e is neg:
            return 1
        else:
            return 2

method main(System.Console sys) -> void:
    sys.out.println(f(-1))
    sys.out.println(f(1.0))
    sys.out.println(f(1.234))
    sys.out.println(f([1, 2, 3]))
