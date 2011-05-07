define msgType1 as {int op, [int] payload}
define msgType2 as {int op, int header, [int] rest}
define msgType as msgType1 | msgType2

string f(msgType msg):
    return str(msg.op)

void System::main([string] args):
    out<->println(f({op:1,payload:[1,2,3]}))

