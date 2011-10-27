import * from whiley.lang.*

// this is a comment!
define c2nat as int where $ < 10

c2nat f(c2nat x):
    x = x + 1
    return x

void ::main(System sys,[string] args):
    debug toString(f(9))
