define tenup as int where $ > 10
define msg1 as {tenup op, [int] data}
define msg2 as {int index}

define msgType as msg1 | msg2

void System::main([string] args):
    msgType x = {op:11,data:[]}
    print str(x)
