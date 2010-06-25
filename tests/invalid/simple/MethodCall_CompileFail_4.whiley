int f(System x, int x):
    return x->get()

int System::get():
    return 1

void System::main([string] args):
    print str(f(this,1))
