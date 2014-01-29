import whiley.lang.System

type expr is {int op, expr lhs} | {string err}

function f(expr e) => int:
    if e is {string err}:
        return |e.err|
    else:
        return -1

method main(System.Console sys) => void:
    int x = f({err: "Hello World"})
    sys.out.println(Any.toString(x))
    x = f({op: 1, lhs: {err: "Gotcha"}})
    sys.out.println(Any.toString(x))
