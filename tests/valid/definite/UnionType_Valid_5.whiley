define msgType1 as (int op, [int] payload) requires op == 1 
define msgType2 as (int op, int header, [int] rest) requires op == 2
define msgType as msgType1 | msgType2

void f(msgType msg):
    print str(msg.op)

void System::main([string] args):
    f((op:1,payload:[1,2,3]))

