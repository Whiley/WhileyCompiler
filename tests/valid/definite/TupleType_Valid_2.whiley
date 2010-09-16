(int,int) f(int x):
    return (x,x+2)

void System::main([string] args):
    (int,int) x = f(1)
    print str(x)
