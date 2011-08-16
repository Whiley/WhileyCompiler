(int,int) f(int x):
    return (x,x+2)

void System::main([string] args):
    x = f(1)
    this.out.println(str(x))
