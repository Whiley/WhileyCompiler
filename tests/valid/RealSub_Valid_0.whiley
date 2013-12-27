import println from whiley.lang.System

real f(real x) requires x >= 0.2345, ensures $ < -0.2:
    return (0.0 - x)

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(1.234)))
