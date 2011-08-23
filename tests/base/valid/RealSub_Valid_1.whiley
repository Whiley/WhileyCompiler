import whiley.lang.*:*

real f(real x):
    return (0.0 - x)

void ::main(System sys,[string] args):
    sys.out.println(str(f(1.234)))
