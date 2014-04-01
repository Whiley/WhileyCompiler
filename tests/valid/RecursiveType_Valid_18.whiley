import whiley.lang.System

type State is {string input, int pos}

type Expr is {int num} | {int op, Expr rhs, Expr lhs} | {string err}

function parse(string input) => Expr:
    {Expr e, State st} r = parseAddSubExpr({input: input, pos: 0})
    return r.e

function parseAddSubExpr(State st) => {Expr e, State st}:
    return {e: {num: 1}, st: st}

method main(System.Console sys) => void:
    Expr e = parse("Hello")
    sys.out.println(Any.toString(e))
