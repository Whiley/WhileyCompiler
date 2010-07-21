int System::g(int x):
    return str(x - 1)

void System::f(int x) where x > this->g(x):
    print str(x)

void System::main([string] args):
    this->f(1)
