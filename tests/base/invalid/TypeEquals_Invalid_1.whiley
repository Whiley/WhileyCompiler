import whiley.lang.*:*

define expr as [int]|int

void System::f(expr e):
    if e is [int]:
        this.out.println("GOT [INT]")
    else if e is int:
        this.out.println("GOT INT")
    else:
        this.out.println("GOT SOMETHING ELSE?")

void System::main([string] args):
    e = 1
    this.f(e)
    e = {x:1,y:2}
    this.f(e)
 
