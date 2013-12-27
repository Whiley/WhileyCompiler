import println from whiley.lang.System

define iset as {int} | int

string f(iset e):
    if e is {int}:
        return "{int}"
    else:
        return "int"

void ::main(System.Console sys):
    sys.out.println(f({1,2,3}))
    sys.out.println(f(1))
