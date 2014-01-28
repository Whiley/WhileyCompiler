import whiley.lang.System

type Expr is {int num} | {int op, Expr rhs, Expr lhs} | {string err}

function parseTerm() => Expr:
    return parseIdentifier()

function parseIdentifier() => Expr:
    return {err: "err"}

method main(System.Console sys) => void:
    Expr e = parseTerm()
    sys.out.println(Any.toString(e))
