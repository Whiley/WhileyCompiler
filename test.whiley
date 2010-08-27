int f(int x, int y) requires x>=0 && y>=0, ensures $>0:
    bool a
    a = x == y
    if(a):
        return 1
    else:
        return x + y

void System::main([string] args):
    print str(f(4,345))
