import whiley.lang.*:*

define BinOp as {Expr lhs, Expr rhs}
define Expr as BinOp | real | [Expr]

int f(Expr e):
    if e is [Expr]:
        return |e|
    else:
        return 0

void System::main([string] args):
    v = f([1.0,2.0,3.0])
    this.out.println(str(v))
    v = f(1.234)
    this.out.println(str(v))
