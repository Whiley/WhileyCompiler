define IntList as {int op, [real] rest}|{int op, int mode}

void f(IntList y):
    out->println(str(y))

void System::main([string] args):
    x = {op:1, rest:[1.23]}
    if |args| == 10:
        x = {op:1.23, mode: 0}
    x.op = 123 // SHOULD BE OK
    f(x)
    
