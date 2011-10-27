import * from whiley.lang.*

real f(real x):
    return (0.0 - x)

void ::main(System sys,[string] args):
    sys.out.println(toString(f(1.234)))
