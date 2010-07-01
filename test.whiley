define expr as {int}|bool

void f(expr e):
    if e ~= bool:
        print "HELLO"

void System::main([string] args):
    expr e = true
    f(e)
    e = {1,2,3,4}
    f(e)
 
