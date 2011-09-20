import * from whiley.lang.*

int f(int x):
    if x > 0:
        skip
    else:
        return -1
    return x

void ::main(System sys,[string] args):
    sys.out.println(str(f(1)))
    sys.out.println(str(f(-10)))
