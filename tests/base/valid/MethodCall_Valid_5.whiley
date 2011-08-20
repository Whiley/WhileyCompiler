import whiley.lang.*:*

define Proc as process { int state }

int Proc::get():
    return this.state

[int] System::f(Proc x):
    return [1,2,3,x.get()]

void System::main([string] args):
    proc = spawn { state: 1 }
    this.out.println(str(this.f(proc)))
