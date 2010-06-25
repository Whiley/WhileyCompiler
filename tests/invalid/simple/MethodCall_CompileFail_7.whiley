define (int x, int y) as wmccf7tup

wmccf7tup f(System x, int x):
    return (x:1,y:x->get())

int System::get():
    return 1

void System::main([string] args):
    print str(f(this,1))
