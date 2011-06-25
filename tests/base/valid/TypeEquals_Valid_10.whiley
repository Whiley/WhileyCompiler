define bop as {int x, int y}
define expr as int|bop

int f(expr e):
    if e is bop:
        return e.x + e.y
    else:
        return e + 1 // type difference

void System::main([string] args):
    x = f(1)
    out.println(str(x))
    x = f({x:4,y:10})   
    out.println(str(x))
