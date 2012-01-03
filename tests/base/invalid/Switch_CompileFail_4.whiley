import * from whiley.lang.*

int f(int x):
    switch x:
        case 1:
            return 0
        case 1:
            return 1
        default:
            return 0
    return 10

void ::main(System sys,[string] args):
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(3)))
    sys.out.println(Any.toString(f(-1)))
