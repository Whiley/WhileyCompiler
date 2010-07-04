// A simple, recursive expression tree
define expr as (int num) | (int op, expr lhs, expr rhs) | (string err)

expr parseIdentifier():
    return (err:"err")

void System::main([string] args):
    expr e = parseIdentifier()
    print str(e)
