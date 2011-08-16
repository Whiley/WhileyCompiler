define test as {int x} | {int y}
define src as test | int

string f(src e):
    if e is test:
        return "{int x} | {int y}"
    else:
        return "int"

void System::main([string] args):
    this.out.println(f({x: 1}))
    this.out.println(f({y: 2}))
    this.out.println(f(1))
