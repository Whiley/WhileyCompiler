define int where $ >= 0 as frf1nat

void f(frf1nat y):
    print "F(NAT)"

void f(int x):
    print "F(INT)"

void System::main([string] args):
    f(-1)
    f(1)
