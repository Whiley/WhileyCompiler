import println from whiley.lang.System

define expr as int | {int op, expr left, expr right}

void ::main(System.Console sys):
    e = {op:1,left:1,right:2}
    sys.out.println(Any.toString(e))
