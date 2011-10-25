import * from whiley.lang.*

int f(int x):
    switch x:
        case 1,2:
            return -1
        case 3:
            return 1
    return 10

void ::main(System sys,[string] args):
    sys.out.println(str(f(1)))
    sys.out.println(str(f(2)))
    sys.out.println(str(f(3)))
    sys.out.println(str(f(-1)))
