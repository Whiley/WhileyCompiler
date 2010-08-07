fr8nat as int where $ > 0
fr8neg as int where $ < 0

void f(fr8nat y):
    print "F(NAT)"

void f(fr8neg x):
    print "F(NEG)"

void System::main([string] args):
    f(-1)
    f(1)
