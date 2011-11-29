import * from whiley.lang.*

int f() ensures 2*$==1:
    return 1

void ::main(System sys,[string] args):
    debug toString(f())
