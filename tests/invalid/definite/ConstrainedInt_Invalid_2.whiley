// this is a comment!
define int where $ < 10 as c2nat

void f(c2nat x):
    c2nat y
    x = x + 1
    y = x
    print str(y)

void System::main([string] args):
    f(9)
