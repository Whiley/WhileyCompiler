define point as {int x, int y} where $.x > 0 && $.y > 0

point f(point x):
    return x

void System::main([string] args):
    p = f({x:1,y:1})
    print str(p)
