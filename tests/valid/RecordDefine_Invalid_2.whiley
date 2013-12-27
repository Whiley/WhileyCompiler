import println from whiley.lang.System

define point as {int x, int y}

point f(point x):
    return x

void ::main(System.Console sys):
    p = f({x:1,y:1})
    sys.out.println(Any.toString(p))
