import * from whiley.lang.*

type expr is int | {int op, expr left, expr right}

function f(expr e) -> expr:
    return e

method main(System.Console sys) -> void:
    expr e = {op: 1, left: "HELLO", right: 2}
    sys.out.println(Any.toString(f(e)))
