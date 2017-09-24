int ADD = 1
int SUB = 2
int MUL = 3
int DIV = 4

type bop is ({int op, int rhs, int lhs} r) where ADD <= r.op && r.op <= DIV

function f(bop b) -> bop:
    return b

public export method test():
    bop b = {op: 0, rhs: 2, lhs: 1}
    f(b)
