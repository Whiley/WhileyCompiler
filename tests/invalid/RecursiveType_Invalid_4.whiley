int ADD = 1
int SUB = 2
int MUL = 3
int DIV = 4

type binop is ({int op, expr left, expr right} r) where r.op == ADD || r.op == SUB || r.op == MUL || r.op == DIV

type expr is int | binop

function f(expr e) -> expr:
    return e

method test() -> expr:
    expr e1 = {op: 0, left: {op: MUL, left: 2, right: 2}, right: 2}
    return f(e1)
