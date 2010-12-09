[int] f(System x, int x):
    return [1,2,3,x->get()]

int System::get():
    return 1

void System::main([string] args):
    print str(f(this,1))
