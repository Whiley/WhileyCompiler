define [int] where no { x in $ | x < 0 } as plistv6 

int f(plistv6 xs):
    return |xs|

int g(plistv6 left, [int] right):
    return f(left + right)

void System::main([string] args):
    int r = g([1,2,3],[6,7,8])
    print str(r)
