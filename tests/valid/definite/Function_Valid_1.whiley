void f(real x):
    print "GOT REAL"
    print str(x)

void f(int x):
    print "GOT INT"
    print str(x)

void System::main([string] args):
    f(1)
    f(1.23)
