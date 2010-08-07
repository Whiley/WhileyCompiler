// this is a comment!
define irf3nat as int requires $ < 10
define pirf3nat as irf3nat requires $ > 0

void f(int x):
    pirf3nat y
    y = x
    print str(y)

void System::main([string] args):
    f(11)
