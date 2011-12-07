import * from whiley.lang.*

define expr as int | {int op, expr left, expr right}

void ::main(System sys,[string] args):
    e = {op:1,left:1,right:2}
    sys.out.println(Any.toString(e))
