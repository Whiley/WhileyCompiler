import * from whiley.lang.*

int System::g(int x):
    return x - 1

void System::f(int x) requires x > this.g(x):
    debug toString(x)

void ::main(System sys,[string] args):
    this.f(1)
