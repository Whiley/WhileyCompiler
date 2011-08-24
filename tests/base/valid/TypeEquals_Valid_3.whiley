import whiley.lang.*:*

define expr as {int}|bool

string f(expr e):
    if e is {int}:
        return "GOT {INT}"
    else:
        return "GOT BOOL"

void ::main(System sys,[string] args):
    e = true
    sys.out.println(f(e))
    e = {1,2,3,4}
    sys.out.println(f(e))
 
