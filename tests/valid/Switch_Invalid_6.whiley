import println from whiley.lang.System

int f(real x):
    switch x:
        case 1.23:
            return 0
        case 2.01:
            return -1
    return 10

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(1.23)))
    sys.out.println(Any.toString(f(2.01)))
    sys.out.println(Any.toString(f(3)))
    sys.out.println(Any.toString(f(-1)))
