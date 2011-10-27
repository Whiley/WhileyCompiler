import * from whiley.lang.*

!null f(int x):
    return x

void ::main(System sys, [string] args):
    sys.out.println(String.toString(f(1)))


