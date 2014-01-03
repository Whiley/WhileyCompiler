import println from whiley.lang.System

type expr is {int} | bool

function f(expr e) => string:
    if e is {int}:
        return "GOT {INT}"
    else:
        return "GOT BOOL"

method main(System.Console sys) => void:
    e = true
    sys.out.println(f(e))
    e = {1, 2, 3, 4}
    sys.out.println(f(e))
