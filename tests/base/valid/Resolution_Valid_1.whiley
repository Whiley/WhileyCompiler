import * from whiley.lang.*

int f(int b):
    return b + 1

void ::main(System sys,[string] args):
    b = f(10)
    sys.out.println(toString(b))
