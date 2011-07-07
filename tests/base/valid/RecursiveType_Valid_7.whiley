// A simple, recursive expression tree
define expr as {int num} | {int op, expr lhs, expr rhs} | {string err}

expr parseTerm():
    return parseIdentifier() 

expr parseIdentifier():
    return {err:"err"}

void System::main([string] args):
    e = parseTerm()
    out.println(str(e))
