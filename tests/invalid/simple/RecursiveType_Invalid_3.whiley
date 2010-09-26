define nat as int where $ >= 0
define expr as nat | {int op, expr left, expr right}

expr f(expr e):
    return e

void System::main([string] args):
    e = -1
    print str(f(e))
