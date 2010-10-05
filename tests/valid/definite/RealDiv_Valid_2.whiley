real g(int x):
     return x / 3

void f(int x, int y) requires x>=0 && y>0:
    out->println(str(g(x)))

void System::main([string] args):
     f(1,2)

