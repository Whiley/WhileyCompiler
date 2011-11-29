import * from whiley.lang.*

real f(real x) requires x > 0, ensures $ < 0:
    return -x

void ::main(System sys,[string] args):
    sys.out.println(toString(f(1.2)))
    sys.out.println(toString(f(0.00001)))
    sys.out.println(toString(f(5632)))
