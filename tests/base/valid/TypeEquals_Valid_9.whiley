define bop as {int x, int y}
define expr as int|bop

int f(expr e):
    if e ~= bop:
        return e.x + e.y
    else:
        return e // type difference

void System::main([string] args):
    x = f(1)
    out.println(str(x))
    x = f({x:4,y:10})   
    out.println(str(x))
