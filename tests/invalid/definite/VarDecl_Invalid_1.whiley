void f(int x) where x >= 0:
    int y = 10 / x
    print str(x)
    print str(y)

void System::main([string] args):
    f(10)
    f(0)
