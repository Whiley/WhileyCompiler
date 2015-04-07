import whiley.lang.*

type bop is {int y, int x}

type expr is int | bop

function f(expr e) -> bool:
    if e is int:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    expr e = 1
    sys.out.println(f(e))
    e = {y: 2, x: 1}
    sys.out.println(f(e))
