int System::f(System x):
    return x->get()

int System::get():
    return 123

void System::main([string] args):
    print str(this->f(this))
