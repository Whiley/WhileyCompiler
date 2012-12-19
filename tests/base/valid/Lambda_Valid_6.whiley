import println from whiley.lang.System

int ::f(int x):
    return x + 1

int g(int::(int) func, int p):
    return func(p)

void ::main(System.Console sys):
    x = g(&(int x -> f(x+1)),5)
    sys.out.println(x)

