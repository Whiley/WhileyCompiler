import whiley.lang.*:*

define expr as {int}|bool

string f(expr e):
    if e is {int}:
        return "GOT {INT}"
    else:
        return "GOT BOOL"

void System::main([string] args):
    e = true
    this.out.println(f(e))
    e = {1,2,3,4}
    this.out.println(f(e))
 
