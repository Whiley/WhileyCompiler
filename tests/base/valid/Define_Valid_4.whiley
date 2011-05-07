define codeOp as { 1, 2, 3, 4 }
define code as {codeOp op, [int] payload}

string f(codeOp x):
    y = {op:x,payload:[]}
    return str(y)

void System::main([string] args):
    out<->println(f(1))
