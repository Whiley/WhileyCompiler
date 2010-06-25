define { 1, 2, 3, 4 } as codeOp
define (codeOp op, [int] payload) as code

void f(codeOp x):
    code y
    y = (op:x,payload:[])
    print str(y)

void System::main([string] args):
    f(1)
