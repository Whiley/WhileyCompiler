define (int x, int y) as wmcr6tup

wmcr6tup System::f(System x, int y):
    return (x:y,y:x->get())

int System::get():
    return 1

void System::main([string] args):
    print str(this->f(this,1))
