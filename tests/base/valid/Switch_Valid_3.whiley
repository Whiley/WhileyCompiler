import whiley.lang.*:*

int f(real x):
    switch x:
        case 1.23:
            return 0
        case 2.01:
            return -1
    return 10

void ::main(System sys,[string] args):
    sys.out.println(str(f(1.23)))
    sys.out.println(str(f(2.01)))
    sys.out.println(str(f(3)))
    sys.out.println(str(f(-1)))
