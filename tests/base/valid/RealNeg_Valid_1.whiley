import * from whiley.lang.*

real f(real x):
    return -x

void ::main(System sys,[string] args):
    sys.out.println(str(f(1.2)))
    sys.out.println(str(f(0.00001)))
    sys.out.println(str(f(5632)))
