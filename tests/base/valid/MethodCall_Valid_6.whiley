define wmcr6tup as {int x, int y}

wmcr6tup System::f(System x, int y):
    return {x:y,y:x->get()}

int System::get():
    return 1

void System::main([string] args):
    out<->println(str(this->f(this,1)))
