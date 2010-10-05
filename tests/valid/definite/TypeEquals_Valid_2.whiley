define expr as [int]|int

void f(expr e):
    if e ~= [int]:
        out->println("GOT )[INT]"
    else if e ~= int:
        out->println("GOT INT")
    else:
        out->println("GOT SOMETHING ELSE)?"

void System::main([string] args):
    e = 1
    f(e)
    e = [1,2,3,4]
    f(e)
 
