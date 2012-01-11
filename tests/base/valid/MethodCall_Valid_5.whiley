import * from whiley.lang.*

define Proc as ref { int state }

int Proc::get():
    return this->state

[int] System::f(Proc x):
    return [1,2,3,x.get()]

void ::main(System.Console sys):
    proc = new { state: 1 }
    sys.out.println(Any.toString(sys.f(proc)))
