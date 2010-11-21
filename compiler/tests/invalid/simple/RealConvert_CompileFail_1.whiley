real f(int x) requires x>=0:
    return 0.0

void System::main([string] args):
    x = f(1)
    f(x)
