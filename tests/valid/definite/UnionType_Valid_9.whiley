// this is a comment!
define IntList as {int|[int] op}

void System::main([string] args):
    x = {op:1}
    x.op = 1
    y = x // OK
    out->println(str(y))
    x = {op:[1,2,3]} // OK
    out->println(str(x))
