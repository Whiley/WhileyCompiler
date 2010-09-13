// this is a comment!
define IntList as {int|[int] op}

void System::main([string] args):
    IntList x = {op:1}
    {int op} y
    {[int] op} z
    x.op = 1
    y = x // OK
    print str(y)
    x.op = [1,2,3] // NOT OK
