import * from whiley.lang.*

define nat as int where $ >= 0
define expr as nat | {int op, expr left, expr right}

void ::main(System sys,[string] args):
    e = 14897
    sys.out.println(str(e))
