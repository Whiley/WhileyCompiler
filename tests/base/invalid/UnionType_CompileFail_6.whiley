import * from whiley.lang.*

// this is a comment!
define IntList as {int|[int] op}

int f({int op} x):
    return x.op

void ::main(System.Console sys,[string] args):
    x = {op:1}
    x.op = 2
    x.op = [1,2,3] // OK
    f(x)
