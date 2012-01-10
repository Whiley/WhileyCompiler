import * from whiley.lang.*

// this is a comment!
define c2nat as int where $ < 10

c2nat f(c2nat x):
    x = x + 1
    return x

void ::main(System.Console sys):
    debug Any.toString(f(9))
