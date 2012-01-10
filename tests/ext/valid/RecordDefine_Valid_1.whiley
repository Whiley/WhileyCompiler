import * from whiley.lang.*

define point as {int x, int y} where $.x > 0 && $.y > 0

point f(point x):
    return x

void ::main(System.Console sys,[string] args):
    p = f({x:1,y:1})
    sys.out.println(Any.toString(p))
