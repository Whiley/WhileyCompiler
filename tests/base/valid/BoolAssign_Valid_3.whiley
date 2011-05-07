int f(int x, int y):
    a = x == y
    if(a):
        return 1
    else:
        return x + y

int g(int x, int y):
    a = x >= y
    if(!a):
        return x + y
    else:
        return 1


void System::main([string] args):
    out<->println(str(f(1,1)))
    out<->println(str(f(0,0)))
    out<->println(str(f(4,345)))
    out<->println(str(g(1,1)))
    out<->println(str(g(0,0)))
    out<->println(str(g(4,345)))
