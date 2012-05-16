import println from whiley.lang.System

define Proc as ref { int state }

int Proc::get():
    return this->state

[int] ::f(Proc x):
    return [1,2,3,x.get()]

void ::main(System.Console sys):
    proc = new { state: 1 }
    sys.out.println(Any.toString(f(proc)))
