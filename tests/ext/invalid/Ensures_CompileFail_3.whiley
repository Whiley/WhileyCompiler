int System::g(int x):
    return str(x - 1)

void System::f(int x) requires x > this->g(x):
    debug str(x)

void System::main([string] args):
    this->f(1)
