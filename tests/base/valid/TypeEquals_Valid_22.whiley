import whiley.lang.*:*

define iset as {int} | int

string f(iset e):
    if e is {int}:
        return "{int}"
    else:
        return "int"

void System::main([string] args):
    this.out.println(f({1,2,3}))
    this.out.println(f(1))
