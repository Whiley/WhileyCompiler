define byte as int where $ >=0 && $ <= 255
define codeOp as { 1, 2, 3, 4 }
define code as {codeOp op, [int] payload}

void f(code x):
    byte y
    y = x.op
    print str(y)

void System::main([string] args):
    f({op:1,payload:[1]})
