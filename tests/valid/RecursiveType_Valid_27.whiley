import whiley.lang.*

type Expr is real | Var | BinOp

type BinOp is {Expr rhs, Expr lhs}

type Var is {[int] id}

type SyntaxError is {[int] err}

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

public method main(System.Console sys) -> void:
    int i = -5
    while i < 10:
        SExpr e = sbuild(i)
        if e is SyntaxError:
            sys.out.println_s("syntax error: " ++ e.err)
        else:
            sys.out.println(e)
        i = i + 1
