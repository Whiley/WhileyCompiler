import println from whiley.lang.System

define wmcr6tup as {int x, int y}
define Proc as ref { int state }

int Proc::get():
    return this->state

wmcr6tup ::f(Proc x, int y):
    return {x:y,y:x.get()}

void ::main(System.Console sys):
    proc = new { state: 1 }
    sys.out.println(Any.toString(f(proc,1)))
