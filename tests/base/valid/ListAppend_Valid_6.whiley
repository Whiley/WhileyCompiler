define plistv6 as [int]

int f(plistv6 xs):
    return |xs|

int g(plistv6 left, plistv6 right):
    return f(left + right)

void System::main([string] args):
    r = g([1,2,3],[6,7,8])
    this.out.println(str(r))
