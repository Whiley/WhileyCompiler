import println from whiley.lang.System

define point as {int x, int y} where $.x > 0 && $.y > 0

void ::main(System.Console sys):
    p = {x:1,y:1}
    sys.out.println(Any.toString(p))
