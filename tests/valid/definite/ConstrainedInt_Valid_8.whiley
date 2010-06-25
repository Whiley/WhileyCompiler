define int where $ >=0 && $ <= 255 as byte
define { 1, 2, 3, 4 } as codeOp
define (codeOp op, [int] payload) as code

void f(code x):
    byte y
    y = x.op
    print str(y)

void System::main([string] args):
    f((op:1,payload:[1]))
