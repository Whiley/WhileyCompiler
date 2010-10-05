// this is a comment!
define ir1nat as int where $ > 0
define pir1nat as ir1nat where $ > 1

void f(int x):
    if x > 2:
        y = x
        out->println(str(y))

void System::main([string] args):
    f(1)
    f(2)
    f(3)
