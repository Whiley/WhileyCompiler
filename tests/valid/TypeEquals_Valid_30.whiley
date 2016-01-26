

constant ADD is 0

constant SUB is 1

constant MUL is 2

constant DIV is 3

type BOp is (int x) where ADD <= x && x <= DIV

type BinOp is {BOp op, Expr rhs, Expr lhs}

type ListAccess is {Expr index, Expr src}

type Expr is int | BinOp | Expr[] | ListAccess

function evaluate(Expr e) -> int:
    if e is int:
        return e
    else:
        if e is BinOp:
            return 2
        else:
            if e is Expr[]:
                return 3
            else:
                if e is ListAccess:
                    int src = evaluate(e.src)
                    int index = evaluate(e.index)
                    return src + index
                else:
                    return -1

public export method test() :
    Expr e = 1
    assume evaluate(e) == 1
    e = {op: ADD, rhs: e, lhs: e}
    assume evaluate(e) == 2
    e = [e]
    assume evaluate(e) == 3
    e = {index: 1, src: e}
    assume evaluate(e) == 4
