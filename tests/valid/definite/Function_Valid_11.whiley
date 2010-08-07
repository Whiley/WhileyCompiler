define msg1 as (int op, int s) requires op == 1
define msg2 as (int op, int s) requires op == 2

void f(msg1 m):
    print "MSG1"

void f(msg2 m):
    print "MSG2"

void System::main([string] args):
    msg1 m1 = (op:1,s:123)
    msg2 m2 = (op:2,s:123)
    f(m1)
    f(m2)
