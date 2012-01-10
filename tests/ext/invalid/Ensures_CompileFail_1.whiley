import * from whiley.lang.*

int f() ensures 2*$==1:
    return 1

void ::main(System.Console sys,[string] args):
    debug Any.toString(f())
