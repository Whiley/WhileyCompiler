define realtup as {real op}

void f(realtup t):
    x = t.op
    print str(t)

void System::main([string] args):
    t = {op:1}
    f(t)
