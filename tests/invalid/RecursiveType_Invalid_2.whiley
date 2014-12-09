
type nat is (int n) where n >= 0

type expr is nat | {int op, expr left, expr right}

function f(expr e) -> expr:
    return e

method main(System.Console sys) -> void:
    e = -1
    debug Any.toString(f(e))
