void g(int z) requires z > 1:
    print str(z)

void f(int x) requires x > 0:
    y = x + 1
    g(y)

void System::main([string] args):
    f(1)
