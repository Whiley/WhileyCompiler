BOp ADD = 0
BOp SUB = 1
BOp MUL = 2
BOp DIV = 3

type BOp is (int x) where ADD <= x && x <= DIV

type BinOp is {BOp op, Expr rhs, Expr lhs}

type Var is {int[] id}

type ListAccess is {Expr index, Expr src}

type Expr is int | BinOp | Expr[] | ListAccess

type Value is int | Value[]

function evaluate(Expr e) -> Value:
    if e is int:
        return e
    else:
        if e is BinOp:
            return evaluate(e.lhs)
        else:
            if e is Expr[]:
                return [0;0]
            else:
                Value src = evaluate(e.src)
                Value index = evaluate(e.index)
                if (src is Value[]) && ((index is int) && ((index >= 0) && (index < |src|))):
                    return src[index]
                else:
                    return 0

public export method test() :
    Expr e = {op: ADD, rhs: 1, lhs: 123}
    Value v = evaluate(e)
    assume v == 123
    e = [1]
    assume evaluate(e) == [0;0]
