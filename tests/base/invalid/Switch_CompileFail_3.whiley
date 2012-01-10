import * from whiley.lang.*

int f(int x):
    switch x:
        default:
            return 0
        default:
            return 1
    return 10

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(3)))
    sys.out.println(Any.toString(f(-1)))
