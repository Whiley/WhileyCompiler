define bop as {int x, int y} where x > 0
define expr as int|bop

int f(expr e):
    if e ~= bop:
        return e.x + e.y
    else if e ~= int:
        return e + 1 // requires type difference
    else:
        return -1 // unreachable

void System::main([string] args):
    x = f(1)
    out<->println(str(x))
    x = f({x:4,y:10})   
    out<->println(str(x))
