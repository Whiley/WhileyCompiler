define expr as [int]|int

void f(expr e):
    if e ~= [int]:
        print "GOT [INT]"
    else if e ~= int:
        print "GOT INT"
    else:
        print "GOT SOMETHING ELSE?"

void System::main([string] args):
    expr e = 1
    f(e)
    e = [1,2,3,4]
    f(e)
 
