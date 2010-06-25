// this is a comment!
define int where $ > 0 as ir1nat
define ir1nat where $ > 1 as pir1nat

void f(int x):
    pir1nat y
    if x > 2:
        y = x
        print str(y)

void System::main([string] args):
    f(1)
    f(2)
    f(3)
