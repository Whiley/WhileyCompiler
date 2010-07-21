int f(int x):
    return x+1

void g(int x, int y) where y == f(x):
    print str(x)
    print str(y)

void System::main([string] args):
    g(1,f(1))
