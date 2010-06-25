// this is a comment!
define int where $ < 10 as cr1nat

void f(cr1nat x):
    cr1nat y
    y = x
    print str(y)

void System::main([string] args):
    f(9)
