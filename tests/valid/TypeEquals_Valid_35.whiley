import whiley.lang.*

type pos is int

type neg is int

type expr is pos | neg | [int]

function f(expr e) -> int:
    if (e is pos) && (e > 0):
        e = e + 1
        return e
    else:
        return 0

method main(System.Console sys) -> void:
    sys.out.println(f(-1) == 0)
    sys.out.println(f(1) == 2)
    sys.out.println(f(1234) == 1235)
