import println from whiley.lang.System

define expr as int | {int op, expr left, expr right}

void ::main(System.Console sys):
    e = 1
    sys.out.println(Any.toString(e))
