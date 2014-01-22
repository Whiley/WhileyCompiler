import println from whiley.lang.System

type Expr is real | {int data, Expr lhs} | [Expr]

type SubExpr is real | {int data, SubExpr lhs}

function toString(Expr e) => string:
    if e is SubExpr:
        if e is real:
            return Any.toString(e)
        else:
            return (Any.toString(e.data) + "->") + toString(e.lhs)
    else:
        return Any.toString(-1)

method main(System.Console sys) => void:
    se1 = 0.1234
    se2 = {data: 1, lhs: se1}
    se3 = {data: 45, lhs: se2}
    e1 = [se1]
    e2 = [e1, se1, se2]
    sys.out.println(toString(se1))
    sys.out.println(toString(se2))
    sys.out.println(toString(se3))
    sys.out.println(toString(e1))
    sys.out.println(toString(e2))
