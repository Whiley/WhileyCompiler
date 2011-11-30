import * from whiley.lang.*

define expr as int | {int op, expr left, expr right}

expr f(expr e):
    return e

void ::main(System sys,[string] args):
    e = {op:1,left:"HELLO",right:2}
    sys.out.println(Any.toString(f(e)))
