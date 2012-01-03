import * from whiley.lang.*

real f(int x):
    return x

void ::main(System sys,[string] args):
    sys.out.println(Any.toString(f(123)))