import whiley.lang.*:*

define Proc as process { int state }

int Proc::get():
    return this.state

int System::f(Proc x):
    return x.get()

void System::main([string] args):
    proc = spawn { state: 123 }
    this.out.println(str(this.f(proc)))
