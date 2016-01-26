

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

function evaluate(Expr e) -> real:
    if e is real:
        return e
    if e is {int[] id}:
        return (real) |e.id|
    else:
        return evaluate(e.lhs) + evaluate(e.rhs)

public export method test() :
    assume evaluate(build(-5)) == 46.0
    assume evaluate(build(-4)) == 41.0
    assume evaluate(build(-3)) == 35.0
    assume evaluate(build(-2)) == 28.0
    assume evaluate(build(-1)) == 20.0
    assume evaluate(build(0)) == 11.0
    assume evaluate(build(1)) == 1.0
    assume evaluate(build(2)) == 2.0
    assume evaluate(build(3)) == 3.0
    assume evaluate(build(4)) == 4.0
    assume evaluate(build(5)) == 5.0
    assume evaluate(build(6)) == 6.0
    assume evaluate(build(7)) == 7.0
    assume evaluate(build(8)) == 8.0
    assume evaluate(build(9)) == 9.0
