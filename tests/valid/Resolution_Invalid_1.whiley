import println from whiley.lang.System

int f(int b):
    return b + 1

void ::main(System.Console sys):
    b = f(10)
    sys.out.println(Any.toString(b))
