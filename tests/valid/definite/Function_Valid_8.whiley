define int where $ > 0 as fr8nat
define int where $ < 0 as fr8neg

void f(fr8nat y):
    print "F(NAT)"

void f(fr8neg x):
    print "F(NEG)"

void System::main([string] args):
    f(-1)
    f(1)
