define (int x, int y) as wmccf6tup

wmccf6tup f(System x, int y):
    return (x:1,y:x->get())

int System::get():
    return 1

void System::main([string] args):
    print str(f(this,1))
