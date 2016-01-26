

type nat is (int n) where n >= 0

type pos is (int p) where p > 0

type expr is nat | {expr rhs, expr lhs}

type posExpr is pos | {posExpr rhs, posExpr lhs}

function f(posExpr e1) -> expr:
    expr e2 = e1
    return e2

public export method test() :
    expr e = f({rhs: 1, lhs: {rhs: 2, lhs: 1}})
    assume e == {lhs:{lhs:1,rhs:2},rhs:1}

