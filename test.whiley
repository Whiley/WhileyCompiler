int g(int x, int y) requires x>=0 && y>=0, ensures $>0:
    bool a
    a = x >= y
    if(!a):
        return x + y
    else:
        return 1


void System::main([string] args):
    print str(g(4,345))
    
