type expr is int | {int op, expr left, expr right}

function f(expr e) -> expr:
    return e

method main() -> expr:
    expr e = {op: 1, left: "HELLO", right: 2}
    return f(e)
