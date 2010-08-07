define IntList as (int op, [real] rest)|(int op, int mode)

void System::main([string] args):
    IntList x = (op:1, rest:[1.23])
    (int op, [real] rest) y
    ([int] op, int mode) z
    print str(x)
    y = x // OK
    x.op = 123 // OK
    print str(x)
    
