define (int op, [int] data) as msg1
define (int op, [(int dum)] data) as msg2

define msg1 | msg2 as msgType

void System::main([string] args):
    msgType x = (op:1,data:[1,2,3])
    [int|(int dum)] list
    print str(x)
    list = x.data
    print str(list)
    
