type Expr is int | Var | BinOp

type BinOp is {Expr rhs, Expr lhs}

type Var is {string id}

type SyntaxError is {string err}

type SExpr is SyntaxError | Expr

function build(int i) -> SExpr:
    if i > 10:
        return {id: "var"}
    else:
        if i > 0:
            return i
        else:
            return {rhs: sbuild(i + 1), lhs: sbuild(i + 10)}

function sbuild(int i) -> SExpr:
    if i > 20:
        return {err: "error"}
    else:
        return build(i)
