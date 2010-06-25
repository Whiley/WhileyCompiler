// this is a comment!
define int where $ > 0 as irf2nat

void f(irf2nat x):
    print str(x)

void g(int x):
    f(x)

void System::main([string] args):
    g(-1)
