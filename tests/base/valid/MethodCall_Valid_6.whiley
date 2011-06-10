define wmcr6tup as {int x, int y}
define Proc as process { int state }

int Proc::get():
    return state

wmcr6tup System::f(Proc x, int y):
    return {x:y,y:x.get()}

void System::main([string] args):
    proc = spawn { state: 1 }
    out.println(str(this.f(proc,1)))
