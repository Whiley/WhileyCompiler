import * from whiley.lang.*

define realtup as {real op}

string f(realtup t):
    x = t.op
    return Any.toString(t)

void ::main(System.Console sys,[string] args):
    t = {op:1}
    sys.out.println(f(t))
