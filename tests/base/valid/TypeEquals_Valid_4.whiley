import println from whiley.lang.System

define expr as {int}|bool

{int} g({int} input):
    return input + {-1}

string f(expr e):
    if e is {int}:
        t = g(e)
        return "GOT: " + Any.toString(t)
    else:
        return "GOT SOMETHING ELSE?"

void ::main(System.Console sys):
    e = {1,2,3,4}
    sys.out.println(f(e))
 
