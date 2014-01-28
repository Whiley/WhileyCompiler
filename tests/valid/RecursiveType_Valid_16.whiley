import whiley.lang.System

type expr is {int num} | {int op, expr rhs, expr lhs} | {string err}

function parseTerm() => expr:
    return parseIdentifier()

function parseIdentifier() => expr:
    return {err: "err"}

method main(System.Console sys) => void:
    e = parseTerm()
    sys.out.println(Any.toString(e))
