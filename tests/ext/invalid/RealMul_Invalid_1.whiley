import * from whiley.lang.*

real f(real x, real y) requires x >= 0.5 && y >= 0.3, ensures $ > 0.65:
    return 0.5 + x*y

void ::main(System sys,[string] args):
    debug Any.toString(f(0.5,0.3))
