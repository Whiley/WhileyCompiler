import println from whiley.lang.System

real f(real x):
    return x + 1

real g(real(int) func):
    return func(1)
    
void ::main(System.Console sys):
    sys.out.println(Any.toString(g(&f)))
