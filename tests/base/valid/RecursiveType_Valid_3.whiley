import * from whiley.lang.*

define nat as int
define expr as nat | {int op, expr left, expr right}

void ::main(System sys,[string] args):
    e = 14897
    sys.out.println(toString(e))
