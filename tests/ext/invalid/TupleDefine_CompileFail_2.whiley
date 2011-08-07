define point as {int x, int y} where $.x > 0 && $.y > 0

point f(point p):
    return p

void System::main([string] args):
    p = {x:1.34,y:1}
    p = f(p)
    debug str(p)
