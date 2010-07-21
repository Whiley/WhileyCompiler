real g(int x):
     return x / 3

void f(int x, int y) where x>=0 && y>0:
    print str(g(x))

void System::main([string] args):
     f(1,2)

