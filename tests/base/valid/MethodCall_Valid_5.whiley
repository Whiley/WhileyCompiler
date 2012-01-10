import * from whiley.lang.*

define Proc as process { int state }

int Proc::get():
    return this->state

[int] System::f(Proc x):
    return [1,2,3,x.get()]

void ::main(System sys,[string] args):
    proc = spawn { state: 1 }
    sys.out.println(Any.toString(sys.f(proc)))
