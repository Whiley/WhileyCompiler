void g(int z) where z > 1:
    print str(z)

void f(int x) where x > 0:
    int y = x + 1
    g(y)

void System::main([string] args):
    f(1)
