import * from whiley.lang.*

// this is a comment!
define irf2nat as int where $ > 0

void f(irf2nat x):
    debug toString(x)

void g(int x):
    f(x)

void ::main(System sys,[string] args):
    g(-1)
