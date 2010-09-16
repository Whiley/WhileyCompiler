// this is a comment!
define IntList as int|[int]

void System::f(int y):
    print str(y)

void System::g([int] z):
    print str(z)

void System::main([string] args):
    x = 123
    this->f(x)
    x = [1,2,3]
    this->g(x)
