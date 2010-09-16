void f(int x) requires x >= 0:
    y = 10 / x
    print str(x)
    print str(y)

void System::main([string] args):
    f(10)
    f(0)
