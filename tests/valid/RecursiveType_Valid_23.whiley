

type BinOp is {Expr rhs, Expr lhs}

type Expr is BinOp | bool | Expr[]

function f(Expr e) -> int:
    if e is Expr[]:
        return |e|
    else:
        return 0

public export method test() :
    int v = f([true,false,true])
    assume v == 3
    v = f(false)
    assume v == 0
