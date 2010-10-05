define IntList as {int op, [real] rest}|{int op, int mode}

void f(IntList y):
    out->println(str(y))

void g({int op, int mode} z):
    out->println(str(z))

void System::main([string] args):
    x = {op:1, rest:[1.23]}
    f(x)
    x = {op:1.23, mode: 0}
    x.op = 123 // OK
    g(x)
    
