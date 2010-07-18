define nat as int where $ >= 0
define expr as nat | (expr lhs, expr rhs)

void System::main([string] args):
    expr x = 1
    x = (lhs:x,rhs:1)

