// this is a comment!
define c1nat as int where $ > 0
define c1pnat as c1nat where $ > 1

c1pnat f(int x):
    return x

void System::main([string] args):
    debug str(f(-1))
