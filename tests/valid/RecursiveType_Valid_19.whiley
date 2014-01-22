import println from whiley.lang.System

type nat is int where $ >= 0

type pos is int where $ > 0

type expr is nat | {expr rhs, expr lhs}

type posExpr is pos | {posExpr rhs, posExpr lhs}

function f(posExpr e1) => expr:
    e2 = e1
    return e2

method main(System.Console sys) => void:
    e = f({rhs: 1, lhs: {rhs: 2, lhs: 1}})
    sys.out.println(Any.toString(e))
