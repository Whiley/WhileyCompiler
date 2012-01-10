import * from whiley.lang.*

define BinOp as {Expr lhs, Expr rhs}
define Expr as BinOp | real | [Expr]

int f(Expr e):
    if e is [Expr]:
        return |e|
    else:
        return 0

void ::main(System.Console sys,[string] args):
    v = f([1.0,2.0,3.0])
    sys.out.println(Any.toString(v))
    v = f(1.234)
    sys.out.println(Any.toString(v))
