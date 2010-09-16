define msg1 as {int op, [int] data}
define msg2 as {int op, [{int dum}] data}

define msgType as msg1 | msg2

void f(msgType m):
    print str(m)

void System::main([string] args):
    x = {op:1,data:[1,2,3]}
    f(x)
    list = x.data
    print str(list)
    
