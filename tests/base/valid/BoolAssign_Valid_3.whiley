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
    this.out.println(str(f(1,1)))
    this.out.println(str(f(0,0)))
    this.out.println(str(f(4,345)))
    this.out.println(str(g(1,1)))
    this.out.println(str(g(0,0)))
    this.out.println(str(g(4,345)))
