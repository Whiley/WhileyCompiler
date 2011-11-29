import * from whiley.lang.*

real f(real x) requires x>0:
    return 0.0

void ::main(System sys,[string] args):
    x = f(1.0)
    f(x)
