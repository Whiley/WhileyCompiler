define IntList as {int op, [real] rest}|{int op, int mode}

void f(IntList y):
    print str(y)

void g({[int] op, int mode} z):
    print str(z)

void System::main([string] args):
    x = {op:1, rest:[1.23]}
    f(x)
    x.op = 123 // OK
    g(x)
    
