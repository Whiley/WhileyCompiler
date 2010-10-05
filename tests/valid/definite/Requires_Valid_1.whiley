int f(int x):
    return x+1

void g(int x, int y) requires y == f(x):
    out->println(str(x))
    out->println(str(y))

void System::main([string] args):
    g(1,f(1))
