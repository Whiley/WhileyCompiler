define posint as int where $ > 0
define negint as int where $ < 0

void f(posint x):
    print "POSITIVE"

void f(negint x):
    print "NEGATIVE"

void f(int x) requires x < 1:
    print "IMPOSSIBLE"

void System::main([string] args):
    f(1)
    f(-1)