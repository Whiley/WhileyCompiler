define tenup as int where $ > 10
define msg1 as {tenup op, [int] data}
define msg2 as {int index}

define msgType as msg1 | msg2

void f(msgType m):
    out->println(str(m))

void System::main([string] args):
    m1 = {op:11,data:[]}
    m2 = {index:1}
    f(m1)
    f(m2)
