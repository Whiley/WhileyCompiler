import whiley.lang.*:*

// this is a comment!
define irf2nat as int where $ > 0

void f(irf2nat x):
    debug str(x)

void g(int x):
    f(x)

void System::main([string] args):
    g(-1)
