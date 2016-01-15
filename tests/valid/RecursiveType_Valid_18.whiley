

type State is {int[] input, int pos}

type Expr is {int num} | {int op, Expr rhs, Expr lhs} | {int[] err}

function parse(int[] input) -> Expr:
    {Expr e, State st} r = parseAddSubExpr({input: input, pos: 0})
    return r.e

function parseAddSubExpr(State st) -> {Expr e, State st}:
    return {e: {num: 1}, st: st}

public export method test() :
    Expr e = parse("Hello")
    assume e == {num:1}

