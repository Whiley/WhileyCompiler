import * from whiley.lang.*

int f(char x):
    return x

void ::main(System.Console sys):
    sys.out.println(Any.toString(f('H')))