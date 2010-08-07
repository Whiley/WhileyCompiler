define fr8nat as int requires $ > 0
define fr8neg as int requires $ < 0

void f(fr8nat y):
    print "F(NAT)"

void f(fr8neg x):
    print "F(NEG)"

void System::main([string] args):
    f(-1)
    f(1)
