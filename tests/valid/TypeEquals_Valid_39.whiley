import println from whiley.lang.System

define expr as {int op, expr lhs} | {string err}

int f(expr e):
    if e is {string err}:
        return |e.err|
    else:
        return -1
    
void ::main(System.Console sys):
    x = f({err:"Hello World"})
    sys.out.println(Any.toString(x))
    x = f({op:1,lhs:{err:"Gotcha"}})
    sys.out.println(Any.toString(x))
