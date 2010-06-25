define int where $ > 10 as tenup
define (tenup op, [int] data) as msg1
define (int index) as msg2

define msg1 | msg2 as msgType

void System::main([string] args):
    msg1 m1 = (op:11,data:[])
    msg2 m2 = (index:1)
    msgType x = m1
    print str(x)
    x = m2
    print str(x)
