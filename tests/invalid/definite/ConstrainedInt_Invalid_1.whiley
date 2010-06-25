// this is a comment!
define int where $ > 0 as c1nat
define c1nat where $ > 1 as c1pnat

void f(int x):
    c1pnat y
    y = x
    print str(y)

void System::main([string] args):
    f(-1)
