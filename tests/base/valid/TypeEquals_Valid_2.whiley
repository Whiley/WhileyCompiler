import println from whiley.lang.System

define expr as [int]|int

string f(expr e):
    if e is [int]:
        return "GOT [INT]"
    else:
        return "GOT INT"

void ::main(System.Console sys):
    e = 1
    sys.out.println(f(e))
    e = [1,2,3,4]
    sys.out.println(f(e))
 
