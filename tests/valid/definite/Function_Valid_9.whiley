void f(int x):
    out->println("F(INT)")

void f(real y):
    out->println("F(REAL)")

void f([int] xs):
    out->println("F()[int])"

void f({int} xs):
    out->println("F(){int})"


void System::main([string] args):
    f(1.0)
    f(1)
    f([1,2,3])
    f({1,2,3})