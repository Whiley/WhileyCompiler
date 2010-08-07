// this is a comment!
define c1nat as int requires $ > 0
define c1pnat as c1nat requires $ > 1

void f(int x):
    c1pnat y
    y = x
    print str(y)

void System::main([string] args):
    f(-1)
