int ADD = 1
int SUB = 2
int MUL = 3
int DIV = 4

type binop is ({int op, expr left, expr right} r) where r.op == ADD || r.op == SUB || r.op == MUL || r.op == DIV
type asbinop is ({int op, expr left, expr right} r) where r.op == ADD || r.op == SUB

type expr is int | binop

type asexpr is int | asbinop

function f(asexpr e) -> asexpr:
    return e

public export method test() -> expr:
    expr e1 = {op: MUL, left: 1, right: 2}
    return f(e1)
