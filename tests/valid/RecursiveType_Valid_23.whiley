import whiley.lang.*

type BinOp is {Expr rhs, Expr lhs}

type Expr is BinOp | real | [Expr]

function f(Expr e) -> int:
    if e is [Expr]:
        return |e|
    else:
        return 0

method main(System.Console sys) -> void:
    int v = f([1.0, 2.0, 3.0])
    assume v == 3
    v = f(1.234)
    assume v == 0
