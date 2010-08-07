int f(int x, int y) requires x>=0 && y>=0 && $>0:
    bool a
    a = x == y
    if(a):
        return 1
    else:
        return x + y

int g(int x, int y) requires x>=0 && y>=0 && $>0:
    bool a
    a = x >= y
    if(!a):
        return x + y
    else:
        return 1


void System::main([string] args):
    print str(f(1,1))
    print str(f(0,0))
    print str(f(4,345))
    print str(g(1,1))
    print str(g(0,0))
    print str(g(4,345))
