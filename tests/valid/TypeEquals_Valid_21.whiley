import println from whiley.lang.System

define expr as {int}|bool

string f(expr e):
    if e is {int}:
        return "GOT {INT}"
    else:
        return "GOT BOOL"

void ::main(System.Console sys):
    e = true
    sys.out.println(f(e))
    e = {1,2,3,4}
    sys.out.println(f(e))
 
