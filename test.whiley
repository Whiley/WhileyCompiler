define IntList as (int|[int] op)

int f(IntList x):
    int y   
    if x ~= (int op):
        y = x.op
    else:
        y = 1
    return y     

void System::main([string] args):
    IntList x = (op:123)
    print str(f(x))
