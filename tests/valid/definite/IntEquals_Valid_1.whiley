void f(int x, real y):
    print str(x)
    print str(y)
    if x == y:
        print "EQUAL"
    else:
        print "NOT EQUAL"

void System::main([string] args):
    f(1,4.0)
    f(1,4.2)
    f(0,0)
