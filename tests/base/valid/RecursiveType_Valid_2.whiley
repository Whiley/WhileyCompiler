import println from whiley.lang.System

define binop as {int op, expr left, expr right}
define expr as int | binop

void ::main(System.Console sys):
    e = 123
    sys.out.println(Any.toString(e))
