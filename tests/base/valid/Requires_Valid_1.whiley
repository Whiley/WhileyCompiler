int f(int x):
    return x+1

(string,string) g(int x, int y) requires y == f(x):
    return (str(x),str(y))

void System::main([string] args):
    (x,y) = g(1,f(1))
    print x
    print y
