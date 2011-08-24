import whiley.lang.*:*

define wmcr6tup as {int x, int y}
define Proc as process { int state }

int Proc::get():
    return this.state

wmcr6tup System::f(Proc x, int y):
    return {x:y,y:x.get()}

void ::main(System sys,[string] args):
    proc = spawn { state: 1 }
    sys.out.println(str(sys.f(proc,1)))
