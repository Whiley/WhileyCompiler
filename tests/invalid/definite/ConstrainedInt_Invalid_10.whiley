// this is a comment!
define int where $ < 10 as irf3nat
define irf3nat where $ > 0 as pirf3nat

void f(int x):
    pirf3nat y
    y = x
    print str(y)

void System::main([string] args):
    f(11)
