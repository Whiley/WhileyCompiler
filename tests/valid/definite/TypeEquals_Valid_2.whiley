define expr as [int]|int

void f(expr e):
    print "GOT HERE"

void System::main([string] args):
    expr e = 1
    f(e)
    e = [1,2,3,4]
    f(e)
 
