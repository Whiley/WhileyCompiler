int add(int x, int y) requires x>=0 && y>=0, ensures $>0:
    if(x == y):
        return 1
    else:
        return x+y

void System::main([string] args):
    print str(1)
