import println from whiley.lang.System

define codeOp as { 1, 2, 3, 4 }
define code as {codeOp op, [int] payload}

string f(code x):
    y = x.op
    return Any.toString(y)

void ::main(System.Console sys):
    sys.out.println(f({op:1,payload:[1]}))
