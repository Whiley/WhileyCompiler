void f(int x):
    if(x < 10):
        print "LESS THAN"
    else if(x > 10):
        print "GREATER THAN"
    else:
        print "EQUALS"

void System::main([string] args):
    f(1)
    f(10)
    f(11)
    f(1212)
    f(-1212)