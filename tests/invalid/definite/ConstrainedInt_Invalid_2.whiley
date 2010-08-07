// this is a comment!
define c2nat as int requires $ < 10

void f(c2nat x):
    c2nat y
    x = x + 1
    y = x
    print str(y)

void System::main([string] args):
    f(9)
