import println from whiley.lang.System

!null&!int f(string x):
    return x

void ::main(System.Console sys):
    sys.out.println(Any.toString(f("Hello World")))
