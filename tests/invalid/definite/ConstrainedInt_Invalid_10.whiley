// this is a comment!
define irf3nat as int where $ < 10
define pirf3nat as irf3nat where $ > 0

void f(int x):
    pirf3nat y
    y = x
    print str(y)

void System::main([string] args):
    f(11)
