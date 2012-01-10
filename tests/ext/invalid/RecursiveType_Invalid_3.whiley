

define nat as int where $ >= 0
define expr as nat | {int op, expr left, expr right}

expr f(expr e):
    return e

void ::main(System.Console sys):
    e = -1
    debug Any.toString(f(e))
