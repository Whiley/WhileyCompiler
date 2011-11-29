import * from whiley.lang.*

real f(int x):
    return x

void ::main(System sys,[string] args):
    sys.out.println(toString(f(123)))