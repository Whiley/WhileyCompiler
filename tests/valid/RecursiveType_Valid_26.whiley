

type Expr is real | {int data, Expr lhs} | Expr[]

type SubExpr is real | {int data, SubExpr lhs}

function toString(Expr e) -> int:
    if e is SubExpr:
        if e is real:
            return 0
        else:
            return 1
    else:
        return -1

public export method test() :
    SubExpr se1 = 0.1234
    SubExpr se2 = {data: 1, lhs: se1}
    SubExpr se3 = {data: 45, lhs: se2}
    Expr e1 = [se1]
    Expr e2 = [e1, se1, se2]
    assume toString(se1) == 0
    assume toString(se2) == 1
    assume toString(se3) == 1
    assume toString(e1) == -1
    assume toString(e2) == -1
