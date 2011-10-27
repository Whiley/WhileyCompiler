import * from whiley.lang.*

!null&!int f(int x):
    return x

void ::main(System sys, [string] args):
    sys.out.println(String.toString(f("Hello World")))
