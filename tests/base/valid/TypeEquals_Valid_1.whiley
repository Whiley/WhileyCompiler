define bop as {int x, int y}
define expr as int|bop

string f(expr e):
    if e is int:
        return "GOT INT"
    else:
        return "GOT BOB"

void System::main([string] args):
    e = 1
    this.out.println(f(e))
    e = {x:1,y:2}
    this.out.println(f(e))
 
