define (int op, int s) where op == 1 as msg1
define (int op, int s) where op == 2 as msg2

void f(msg1 m):
    print "MSG1"

void f(msg2 m):
    print "MSG2"

void System::main([string] args):
    msg1 m1 = (op:1,s:123)
    msg2 m2 = (op:2,s:123)
    f(m1)
    f(m2)
