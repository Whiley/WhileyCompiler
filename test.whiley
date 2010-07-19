define nat as int where $ >= 0
define onion as [int]|nat
define expr as nat | (expr lhs, expr rhs)

void System::main([string] args):
    onion x = |args|
    expr e2 = (lhs:(lhs:1,rhs:2),rhs:2)
