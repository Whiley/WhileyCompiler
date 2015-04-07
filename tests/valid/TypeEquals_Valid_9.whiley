import whiley.lang.*

type expr is [int] | int

function f(expr e) -> bool:
    if e is [int]:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    expr e = 1
    sys.out.println(f(e))
    e = [1, 2, 3, 4]
    sys.out.println(f(e))
