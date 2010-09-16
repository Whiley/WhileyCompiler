(int,int) f(int x):
    return (x,x+2)

void System::main([string] args):
    (int x,int y) = f(1)
    print str(x)
    print str(y)
