import println from whiley.lang.*

type state is {string input, int pos} where (pos >= 0) && (pos <= |input|)

type expr is {int num} | {int op, expr rhs, expr lhs} | {string err}

function parse(string input) => expr:
    r = parseAddSubExpr({input: input, pos: 0})
    return r.e

function parseAddSubExpr(state st) => {expr e, state st}:
    return {e: {num: 1}, st: st}

method main(System.Console sys) => void:
    e = parse("Hello")
    sys.out.println(Any.toString(e))
