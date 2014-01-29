import whiley.lang.System

type expr is [int] | int

function f(expr e) => string:
    if e is [int]:
        return "GOT [INT]"
    else:
        return "GOT INT"

method main(System.Console sys) => void:
    expr e = 1
    sys.out.println(f(e))
    e = [1, 2, 3, 4]
    sys.out.println(f(e))
