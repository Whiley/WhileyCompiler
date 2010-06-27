define intup as (int op)
define realtup as (real op)

void f(realtup t):
    real x = t.op
    print str(t)

void System::main([string] args):
    intup t = (op:1)
    f(t)
