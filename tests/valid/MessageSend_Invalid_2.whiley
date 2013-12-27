import println from whiley.lang.System

define Proc as ref { int state }

int Proc::get():
    return this->state

int ::f(Proc x):
    return x.get()

void ::main(System.Console sys):
    proc = new { state: 123 }
    sys.out.println(Any.toString(f(proc)))
