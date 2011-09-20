import * from whiley.lang.*

define realtup as {real op}

string f(realtup t):
    x = t.op
    return str(t)

void ::main(System sys,[string] args):
    t = {op:1}
    sys.out.println(f(t))
