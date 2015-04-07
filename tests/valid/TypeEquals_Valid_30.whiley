import whiley.lang.*

constant ADD is 0

constant SUB is 1

constant MUL is 2

constant DIV is 3

type BOp is (int x) where x in {ADD, SUB, MUL, DIV}

type BinOp is {BOp op, Expr rhs, Expr lhs}

type ListAccess is {Expr index, Expr src}

type Expr is int | BinOp | [Expr] | ListAccess

function evaluate(Expr e) -> int:
    if e is int:
        return e
    else:
        if e is BinOp:
            return 2
        else:
            if e is [Expr]:
                return 3
            else:
                if e is ListAccess:
                    int src = evaluate(e.src)
                    int index = evaluate(e.index)
                    return src + index
                else:
                    return -1

method main(System.Console sys) -> void:
    Expr e = 1
    sys.out.println(evaluate(e))
    e = {op: ADD, rhs: e, lhs: e}
    sys.out.println(evaluate(e))
    e = [e]
    sys.out.println(evaluate(e))
    e = {index: 1, src: e}
    sys.out.println(evaluate(e))
