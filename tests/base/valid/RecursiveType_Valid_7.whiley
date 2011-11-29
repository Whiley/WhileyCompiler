import * from whiley.lang.*

// A simple, recursive expression tree
define expr as {int num} | {int op, expr lhs, expr rhs} | {string err}

expr parseTerm():
    return parseIdentifier() 

expr parseIdentifier():
    return {err:"err"}

void ::main(System sys,[string] args):
    e = parseTerm()
    sys.out.println(toString(e))
