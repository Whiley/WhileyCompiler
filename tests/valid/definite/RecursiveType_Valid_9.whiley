define nat as int requires $ >= 0
define pos as int requires $ > 0

define expr as nat | (expr lhs, expr rhs)
define posExpr as pos | (posExpr lhs, posExpr rhs)

expr f(posExpr e1):
    expr e2 = e1
    return e2

void System::main([string] args):
    expr e = f((lhs:(lhs:1,rhs:2),rhs:1))
    print str(e)
