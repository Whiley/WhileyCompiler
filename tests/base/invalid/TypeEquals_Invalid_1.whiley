define expr as [int]|int

void System::f(expr e):
    if e ~= [int]:
        out->println("GOT [INT]")
    else if e ~= int:
        out->println("GOT INT")
    else:
        out->println("GOT SOMETHING ELSE?")

void System::main([string] args):
    e = 1
    this->f(e)
    e = {x:1,y:2}
    this->f(e)
 
