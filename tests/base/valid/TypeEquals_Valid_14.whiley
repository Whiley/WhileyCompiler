import * from whiley.lang.*

define expr as {int op, expr lhs} | {string err}

int f(expr e):
    if e is {string err}:
        return |e.err|
    else:
        return -1
    
void ::main(System sys,[string] args):
    x = f({err:"Hello World"})
    sys.out.println(str(x))
    x = f({op:1,lhs:{err:"Gotcha"}})
    sys.out.println(str(x))
