

type Expr is int | Var | BinOp

type BinOp is {Expr rhs, Expr lhs}

type Var is {int[] id}

type SyntaxError is {int[] err}

type SExpr is SyntaxError | Expr

function build(int i) -> Expr:
    if i > 10:
        return {id: "var"}
    else:
        if i > 0:
            return i
        else:
            return {rhs: build(i + 1), lhs: build(i + 10)}

function sbuild(int i) -> SExpr:
    if i > 20:
        return {err: "error"}
    else:
        return build(i)

public export method test() :
    assume sbuild(-5) == {lhs:5,rhs:{lhs:6,rhs:{lhs:7,rhs:{lhs:8,rhs:{lhs:9,rhs:{lhs:10,rhs:1}}}}}}
    assume sbuild(-4) == {lhs:6,rhs:{lhs:7,rhs:{lhs:8,rhs:{lhs:9,rhs:{lhs:10,rhs:1}}}}}
    assume sbuild(-3) == {lhs:7,rhs:{lhs:8,rhs:{lhs:9,rhs:{lhs:10,rhs:1}}}}
    assume sbuild(-2) == {lhs:8,rhs:{lhs:9,rhs:{lhs:10,rhs:1}}}
    assume sbuild(-1) == {lhs:9,rhs:{lhs:10,rhs:1}}
    assume sbuild(0) == {lhs:10,rhs:1}
    assume sbuild(1) == 1
    assume sbuild(2) == 2
    assume sbuild(3) == 3
    assume sbuild(4) == 4
    assume sbuild(5) == 5
    assume sbuild(6) == 6
    assume sbuild(7) == 7
    assume sbuild(8) == 8
    assume sbuild(9) == 9
