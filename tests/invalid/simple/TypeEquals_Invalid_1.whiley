define expr as [int]|int

void f(expr e):
    if e ~= [int]:
        print "GOT [INT]"
    else if e ~= int:
        print "GOT INT"
    else:
        print "GOT SOMETHING ELSE?"

void System::main([string] args):
    e = 1
    f(e)
    e = (x:1,y:2)
    f(e)
 
