define tenup as int
define msg1 as {tenup op, [int] data}
define msg2 as {int index}

define msgType as msg1 | msg2

string f(msgType m):
    return str(m)

void System::main([string] args):
    x = {op:11,data:[]}
    out.println(f(x))
