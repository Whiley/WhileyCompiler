import * from whiley.lang.*

define iset as {int} | int

string f(iset e):
    if e is {int}:
        return "{int}"
    else:
        return "int"

void ::main(System sys,[string] args):
    sys.out.println(f({1,2,3}))
    sys.out.println(f(1))
