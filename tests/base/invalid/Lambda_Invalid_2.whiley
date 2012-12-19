import println from whiley.lang.System

int ::f(int x):
    return x + 1

int g(int p):
    func = &(int x -> f(x+1))
    return func(p)

void ::main(System.Console sys):
    x = g(5)
    sys.out.println(x)

