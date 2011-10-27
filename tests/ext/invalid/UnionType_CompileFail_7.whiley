import * from whiley.lang.*

// The effective type of an IntList is (int op)|([int] op)
define IntList as {int op, [real] rest}|{[int] op, int mode}

IntList f(IntList il):
    il.op = 1 
    return il // NOT OK, (int op, but mode may not be defined)

void ::main(System sys,[string] args):
    x = {op:[1], mode:1}
    debug toString(x)
    x = f(x)
    debug toString(x)  

