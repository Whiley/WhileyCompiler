define (int op, [int] payload) where op == 1 as msgType1 
define (int op, int header, [int] rest) where op == 2 as msgType2
define msgType1 | msgType2 as msgType

void f(msgType msg):
    print str(msg.op)

void System::main([string] args):
    f((op:1,payload:[1,2,3]))

