define expr as (int op, expr lhs) | (string err)

int f(expr e):
    if e ~= (string err):
        return |e.err|
    else:
        return -1
    
void System::main([string] args):
    int x = f((err:"Hello World"))
    print str(x)
    x = f((op:1,lhs:(err:"Gotcha")))
    print str(x)
