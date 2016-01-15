

type Expr is real | Var | BinOp

type BinOp is {Expr rhs, Expr lhs}

type Var is {int[] id}

type SyntaxError is {int[] err}

type SExpr is SyntaxError | Expr

function build(int i) -> Expr:
    if i > 10:
        return {id: "var"}
    else:
        if i > 0:
            return (real) i
        else:
            return {rhs: build(i + 1), lhs: build(i + 10)}

function sbuild(int i) -> SExpr:
    if i > 20:
        return {err: "error"}
    else:
        return build(i)

public export method test() :
    assume sbuild(-5) == {lhs:5.0,rhs:{lhs:6.0,rhs:{lhs:7.0,rhs:{lhs:8.0,rhs:{lhs:9.0,rhs:{lhs:10.0,rhs:1.0}}}}}}
    assume sbuild(-4) == {lhs:6.0,rhs:{lhs:7.0,rhs:{lhs:8.0,rhs:{lhs:9.0,rhs:{lhs:10.0,rhs:1.0}}}}}
    assume sbuild(-3) == {lhs:7.0,rhs:{lhs:8.0,rhs:{lhs:9.0,rhs:{lhs:10.0,rhs:1.0}}}}
    assume sbuild(-2) == {lhs:8.0,rhs:{lhs:9.0,rhs:{lhs:10.0,rhs:1.0}}}
    assume sbuild(-1) == {lhs:9.0,rhs:{lhs:10.0,rhs:1.0}}
    assume sbuild(0) == {lhs:10.0,rhs:1.0}
    assume sbuild(1) == 1.0
    assume sbuild(2) == 2.0
    assume sbuild(3) == 3.0
    assume sbuild(4) == 4.0
    assume sbuild(5) == 5.0
    assume sbuild(6) == 6.0
    assume sbuild(7) == 7.0
    assume sbuild(8) == 8.0
    assume sbuild(9) == 9.0
