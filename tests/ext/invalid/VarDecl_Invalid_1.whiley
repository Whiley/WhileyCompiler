void f(int x) requires x >= 0:
    y = 10 / x
    debug str(x)
    debug str(y)

void System::main([string] args):
    f(10)
    f(0)
