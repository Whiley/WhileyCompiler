define codeOp as { 1, 2, 3, 4 }
define code as (codeOp op, [int] payload)

void f(codeOp x):
    code y
    y = (op:x,payload:[])
    print str(y)

void System::main([string] args):
    f(1)
