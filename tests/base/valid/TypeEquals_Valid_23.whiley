define test as {int x} | {int y}
define src as test | int

string f(src e):
    if e ~= test:
        return "{int x} | {int y}"
    else:
        return "int"

void System::main([string] args):
    out.println(f({x: 1}))
    out.println(f({y: 2}))
    out.println(f(1))
