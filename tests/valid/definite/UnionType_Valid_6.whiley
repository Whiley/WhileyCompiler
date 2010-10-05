define tenup as int where $ > 10
define msg1 as {tenup op, [int] data}
define msg2 as {int index}

define msgType as msg1 | msg2

void f(msgType m):
    out->println(str(m))

void System::main([string] args):
    x = {op:11,data:[]}
    f(x)
