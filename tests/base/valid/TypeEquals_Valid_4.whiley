import * from whiley.lang.*

define expr as {int}|bool

{int} g({int} input):
    return input + {-1}

string f(expr e):
    if e is {int}:
        t = g(e)
        return "GOT: " + str(t)
    else:
        return "GOT SOMETHING ELSE?"

void ::main(System sys,[string] args):
    e = {1,2,3,4}
    sys.out.println(f(e))
 
