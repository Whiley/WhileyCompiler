import println from whiley.lang.System

// A simple, recursive expression tree
define expr as {int num} | {int op, expr lhs, expr rhs} | {string err}

expr parseTerm():
    return parseIdentifier() 

expr parseIdentifier():
    return {err:"err"}

void ::main(System.Console sys):
    e = parseTerm()
    sys.out.println(Any.toString(e))
