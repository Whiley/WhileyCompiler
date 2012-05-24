import println from whiley.lang.System

int f(char x):
    return x

void ::main(System.Console sys):
    sys.out.println(Any.toString(f('H')))
