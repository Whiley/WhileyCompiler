define int where $ > 0 as posint
define int where $ < 0 as negint

void f(posint x):
    print "POSITIVE"

void f(negint x):
    print "NEGATIVE"

void f(int x) requires x < 1:
    print "IMPOSSIBLE"

void System::main([string] args):
    f(1)
    f(-1)