import println from whiley.lang.System

int f(int x) requires x > 0:
    return x + 1

int g(int p) requires p >= 0:
    func = &(int x -> f(x+1))
    return func(p)

void ::main(System.Console sys):
    x = g(5)
    sys.out.println(x)

