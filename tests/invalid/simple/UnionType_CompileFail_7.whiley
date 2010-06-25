// The effective type of an IntList is (int op)|([int] op)
define (int op, [real] rest)|([int] op, int mode) as IntList

IntList f(IntList il):
    il.op = 1 // NOT OK, (mode may not be defined)
    return il

void System::main([string] args):
    IntList x = (op:[1], mode:1)
    print str(x)
    x = f(x)
    print str(x)  

