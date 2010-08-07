define tup as (int x, int y)
define point as (int x, int y) where $.x > 0 && $.y > 0

void System::main([string] args):
    point p
    tup z = (x:1,y:-2)
    p = z
    print str(p)
