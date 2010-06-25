define (int op) as intup
define (real op) as realtup

void f(realtup t):
    real x = t.op
    print str(t)

void System::main([string] args):
    intup t = (op:1)
    f(t)
