define nat as int where $ >= 0
define expr as nat | (expr lhs, expr rhs)

void System::main([string] args):
    expr e2 = (lhs:(lhs:1,rhs:-1),rhs:1)
