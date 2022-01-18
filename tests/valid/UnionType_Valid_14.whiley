final BOp ADD = 0
final BOp SUB = 1
final BOp MUL = 2
final BOp DIV = 3

type BOp is (int op) where op == ADD || op ==  SUB || op ==  MUL || op ==  DIV

type BinOp is {BOp op, Expr rhs, Expr lhs}

type ListAccess is {Expr index, Expr src}

type Expr is int | BinOp | Expr[] | ListAccess

function f(Expr e) -> int:
    if e is int:
        return e
    else:
        if e is int[]:
            return |e|
        else:
            return 1

public export method test() :
    assume f(1) == 1
    assume f([1, 2, 3]) == 3
    assume f({op: ADD, rhs: 2, lhs: 1}) == 1
