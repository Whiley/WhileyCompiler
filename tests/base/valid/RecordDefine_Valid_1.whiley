define point as {int x, int y}

point f(point x):
    return x

void System::main([string] args):
    p = f({x:1,y:1})
    out.println(str(p))
