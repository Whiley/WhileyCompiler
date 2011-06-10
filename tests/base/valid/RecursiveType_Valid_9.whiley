define nat as int
define pos as int

define expr as nat | {expr lhs, expr rhs}
define posExpr as pos | {posExpr lhs, posExpr rhs}

expr f(posExpr e1):
    e2 = e1
    return e2

void System::main([string] args):
    e = f({lhs:{lhs:1,rhs:2},rhs:1})
    out.println(str(e))
