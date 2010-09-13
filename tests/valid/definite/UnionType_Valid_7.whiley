define tenup as int where $ > 10
define msg1 as {tenup op, [int] data}
define msg2 as {int index}

define msgType as msg1 | msg2

void System::main([string] args):
    msg1 m1 = {op:11,data:[]}
    msg2 m2 = {index:1}
    msgType x = m1
    print str(x)
    x = m2
    print str(x)
