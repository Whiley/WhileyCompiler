import println from whiley.lang.System

int f(int x) requires x != 0, ensures $ != 1:
    return x

void ::main(System.Console sys):
    sys.out.println(f(9))

