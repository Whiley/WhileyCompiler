define expr as [int]|int

string f(expr e):
    if e is [int]:
        return "GOT [INT]"
    else:
        return "GOT INT"

void System::main([string] args):
    e = 1
    this.out.println(f(e))
    e = [1,2,3,4]
    this.out.println(f(e))
 
