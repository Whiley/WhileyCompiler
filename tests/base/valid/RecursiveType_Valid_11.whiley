define BinOp as {Expr lhs, Expr rhs}
define Expr as BinOp | real | [Expr]

int f(Expr e):
    if e ~= [Expr]:
        return |e|
    else:
        return 0

void System::main([string] args):
    v = f([1.0,2.0,3.0])
    out.println(str(v))
    v = f(1.234)
    out.println(str(v))
