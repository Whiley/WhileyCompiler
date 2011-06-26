int f(int x):
    return x+1


int g(int x, int y):
    return x+y

void System::main([string] args):
    a = 2
    b = 1
    if |args| == 0:
        a = f(b)
    x = g(a,b)
    out.println(str(x))
