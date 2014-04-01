import whiley.lang.System

type bop is {int y, int x}

type expr is int | bop

function f(expr e) => string:
    if e is int:
        return "GOT INT"
    else:
        return "GOT BOB"

method main(System.Console sys) => void:
    expr e = 1
    sys.out.println(f(e))
    e = {y: 2, x: 1}
    sys.out.println(f(e))
