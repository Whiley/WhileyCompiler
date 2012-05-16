import println from whiley.lang.System

int f(int x):
    return x / 3

public void ::main(System.Console sys):
    sys.out.println(Any.toString(f(10)))
