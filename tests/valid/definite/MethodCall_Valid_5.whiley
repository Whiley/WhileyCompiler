[int] System::f(System x):
    return [1,2,3,x->get()]

int System::get():
    return 1

void System::main([string] args):
    out->println(str(this->f(this)))
