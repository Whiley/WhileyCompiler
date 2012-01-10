import * from whiley.lang.*

define expr as int | {int op, expr left, expr right}

void ::main(System.Console sys,[string] args):
    e = 1
    sys.out.println(Any.toString(e))
