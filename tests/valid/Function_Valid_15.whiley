import println from whiley.lang.System

define msg1 as {int op, int s} where op == 1
define msg2 as {int op, int s} where op == 2

string f(msg1 m):
    return ("MSG1")

string f(msg2 m):
    return ("MSG2")

void ::main(System.Console sys):
    m1 = {op:1,s:123}
    m2 = {op:2,s:123}
    sys.out.println(f(m1))
    sys.out.println(f(m2))
