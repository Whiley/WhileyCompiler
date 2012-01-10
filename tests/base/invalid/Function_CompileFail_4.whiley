import * from whiley.lang.*

int f(int x):
    sys.out.println(Any.toString(x))

void ::main(System.Console sys,[string] args):
    f(1)
