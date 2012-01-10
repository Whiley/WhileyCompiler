import * from whiley.lang.*

define Proc as process { int state }

int Proc::get():
    return this->state

int System::f(Proc x):
    return x.get()

void ::main(System.Console sys,[string] args):
    proc = spawn { state: 123 }
    sys.out.println(Any.toString(sys.f(proc)))
