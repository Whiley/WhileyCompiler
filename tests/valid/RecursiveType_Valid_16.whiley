

type Expr is {int num} | {int op, Expr rhs, Expr lhs} | {int[] err}

function parseTerm() -> Expr:
    return parseIdentifier()

function parseIdentifier() -> Expr:
    return {err: "err"}

public export method test() :
    Expr e = parseTerm()
    assume e == {err: "err"}
