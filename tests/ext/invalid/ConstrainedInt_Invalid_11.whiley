import * from whiley.lang.*

// this is a comment!
define odd as { 1,3,5 }

int f(odd x):
    return x

void ::main(System.Console sys):
    y = 2
    f(y)
    debug Any.toString(y)
