int g(int x):
    return x + 1 

void f(int x):
    int y
    y = g(x)

void System::main([string] args):
    f(1)