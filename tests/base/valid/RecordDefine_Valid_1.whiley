import whiley.lang.*:*

define point as {int x, int y}

point f(point x):
    return x

void ::main(System sys,[string] args):
    p = f({x:1,y:1})
    sys.out.println(str(p))
