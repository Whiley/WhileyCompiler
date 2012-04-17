import println from whiley.lang.System

define bop as {int x, int y} where x > 0
define expr as int|bop

string f(expr e):
    if e is int:
        return "GOT INT"
    else:
        return "GOT BOB"

void ::main(System.Console sys):
    e = 1
    sys.out.println(f(e))
    e = {x:1,y:2}
    sys.out.println(f(e))
 
