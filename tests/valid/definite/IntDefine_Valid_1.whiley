// this is a comment!
define ir1nat as int requires $ > 0
define pir1nat as ir1nat requires $ > 1

void f(int x):
    pir1nat y
    if x > 2:
        y = x
        print str(y)

void System::main([string] args):
    f(1)
    f(2)
    f(3)
