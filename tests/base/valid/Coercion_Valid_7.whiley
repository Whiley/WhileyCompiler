import * from whiley.lang.*

int f(int|bool x):
    if x is int:
        return x
    else:
        return 1 

void ::main(System sys,[string] args):
    sys.out.println(str(f(true)))
    sys.out.println(str(f(123)))
