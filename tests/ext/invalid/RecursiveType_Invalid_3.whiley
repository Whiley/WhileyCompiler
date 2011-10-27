import * from whiley.lang.*

define nat as int where $ >= 0
define expr as nat | {int op, expr left, expr right}

expr f(expr e):
    return e

void ::main(System sys,[string] args):
    e = -1
    debug toString(f(e))
