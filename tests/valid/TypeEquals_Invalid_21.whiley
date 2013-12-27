import println from whiley.lang.System

define test as {int x} | {int y}
define src as test | int

string f(src e):
    if e is test:
        return "{int x} | {int y}"
    else:
        return "int"

void ::main(System.Console sys):
    sys.out.println(f({x: 1}))
    sys.out.println(f({y: 2}))
    sys.out.println(f(1))
