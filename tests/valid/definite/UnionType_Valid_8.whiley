define msg1 as {int op, [int] data}
define msg2 as {int op, [{int dum}] data}

define msgType as msg1 | msg2

void System::main([string] args):
    msgType x = {op:1,data:[1,2,3]}
    [int|{int dum}] list
    print str(x)
    list = x.data
    print str(list)
    
