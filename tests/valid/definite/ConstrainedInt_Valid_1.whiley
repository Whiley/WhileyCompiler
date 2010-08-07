// this is a comment!
define cr1nat as int where $ < 10

void f(cr1nat x):
    cr1nat y
    y = x
    print str(y)

void System::main([string] args):
    f(9)
