// The effective type of an IntList is (int op)|([int] op)
define (int op, [real] rest)|([int] op, int mode) as IntList

void System::main([string] args):
    IntList x = (op:1, rest:[1.23])
    (int op, [real] rest) y
    ([int] op, int mode) z
    print x
    y = x // OK
    x.op = [1,2,3] // NOT OK (mode not defined)
    

