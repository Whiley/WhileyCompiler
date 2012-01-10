import * from whiley.lang.*

define expr as [int]|int

void System::f(expr e):
    if e is [int]:
        sys.out.println("GOT [INT]")
    else if e is int:
        sys.out.println("GOT INT")
    else:
        sys.out.println("GOT SOMETHING ELSE?")

void ::main(System.Console sys,[string] args):
    e = 1
    this.f(e)
    e = {x:1,y:2}
    this.f(e)
 
