
type nat is (int n) where n >= 0

type expr is nat | {int op, expr left, expr right}

function f(expr e) -> expr:
    return e

public export method test() -> expr:
    return f(-1)
