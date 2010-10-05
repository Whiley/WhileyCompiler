(int,int) f(int x):
    return (x,x+2)

void System::main([string] args):
    (x,y) = f(1)
    out->println(str(x))
    out->println(str(y))
