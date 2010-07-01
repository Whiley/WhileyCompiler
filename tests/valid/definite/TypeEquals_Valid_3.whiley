define expr as {int}|bool

void f(expr e):
    if e ~= {int}:
        print "GOT {INT}"
    else if e ~= bool:
        print "GOT BOOL"
    else:
        print "GOT SOMETHING ELSE?"

void System::main([string] args):
    expr e = true
    f(e)
    e = {1,2,3,4}
    f(e)
 
