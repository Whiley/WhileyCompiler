import * from whiley.lang.*

define tup as {int x, int y}
define point as {int x, int y} where $.x > 0 && $.y > 0

point f(point p):
    return p

void ::main(System sys,[string] args):
    z = {x:1,y:-2}
    p = f(z)
    debug toString(p)
