void f(int x) requires x > 0:
    print "GOT HERE"

void System::main([string] args):
    f(-1)