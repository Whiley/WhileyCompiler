public int ADD = 1
public int SUB = 2
public int MUL = 3
public int DIV = 4

public type binop is ({int op, expr left, expr right} r) where r.op == ADD || r.op == SUB || r.op == MUL || r.op == DIV
type asbinop is ({int op, expr left, expr right} r) where r.op == ADD || r.op == SUB

public type expr is int | binop

type asexpr is int | asbinop

function f(asexpr e) -> asexpr:
    return e

public export method test() -> expr:
    expr e1 = {op: MUL, left: 1, right: 2}
    return (expr) f((asexpr) e1)
