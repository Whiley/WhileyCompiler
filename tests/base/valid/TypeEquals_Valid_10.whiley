import * from whiley.lang.*

define bop as {int x, int y}
define expr as int|bop

int f(expr e):
    if e is bop:
        return e.x + e.y
    else:
        return e + 1 // type difference

void ::main(System sys,[string] args):
    x = f(1)
    sys.out.println(toString(x))
    x = f({x:4,y:10})   
    sys.out.println(toString(x))
