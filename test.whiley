define bop as (int x, int y) where x > 0
define expr as int|bop

void f(expr e):
    if e ~= int:
        print "GOT INT"
    else if e ~= bop:
        print "GOT BOB"
    else:
        print "GOT SOMETHING ELSE?"

void System::main([string] args):
    expr e = 1
    f(e)
    e = (x:1,y:2)
    f(e)
 
