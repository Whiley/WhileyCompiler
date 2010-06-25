void f(int x):
    print "F(INT)"

void f(real y):
    print "F(REAL)"

void f([int] xs):
    print "F([int])"

void f({int} xs):
    print "F({int})"


void System::main([string] args):
    f(1.0)
    f(1)
    f([1,2,3])
    f({1,2,3})