import whiley.lang.*:*

// The effective type of an IntList is (int op)|([int] op)
define IntList as {int op, [real] rest}|{[int] op, int mode}

IntList f(IntList x):
    return x

void ::main(System sys,[string] args):
    x = {op:1, rest:[1.23]}
    x.op = [1,2,3]
    f(x)  // NOT OK (mode not defined)

