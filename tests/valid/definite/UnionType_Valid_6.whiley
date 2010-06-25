define int where $ > 10 as tenup
define (tenup op, [int] data) as msg1
define (int index) as msg2

define msg1 | msg2 as msgType

void System::main([string] args):
    msgType x = (op:11,data:[])
    print str(x)
