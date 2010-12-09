// this is a comment!
define c2nat as int where $ < 10

c2nat f(c2nat x):
    x = x + 1
    return x

void System::main([string] args):
    print str(f(9))
