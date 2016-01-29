

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

function evaluate(Expr e) -> int:
    if e is int:
        return e
    if e is {int[] id}:
        return |e.id|
    else:
        return evaluate(e.lhs) + evaluate(e.rhs)

public export method test() :
    assume evaluate(build(-5)) == 46
    assume evaluate(build(-4)) == 41
    assume evaluate(build(-3)) == 35
    assume evaluate(build(-2)) == 28
    assume evaluate(build(-1)) == 20
    assume evaluate(build(0)) == 11
    assume evaluate(build(1)) == 1
    assume evaluate(build(2)) == 2
    assume evaluate(build(3)) == 3
    assume evaluate(build(4)) == 4
    assume evaluate(build(5)) == 5
    assume evaluate(build(6)) == 6
    assume evaluate(build(7)) == 7
    assume evaluate(build(8)) == 8
    assume evaluate(build(9)) == 9
