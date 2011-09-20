import * from whiley.lang.*

define codeOp as { 1, 2, 3, 4 }
define code as {codeOp op, [int] payload}

string f(code x):
    y = x.op
    return str(y)

void ::main(System sys,[string] args):
    sys.out.println(f({op:1,payload:[1]}))
