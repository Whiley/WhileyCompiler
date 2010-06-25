define (int x, int y) where $.x > 0 && $.y > 0 as point

void System::main([string] args):
    point p
    p = (x:1,y:1)
    print str(p)
