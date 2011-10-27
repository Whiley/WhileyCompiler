import * from whiley.lang.*

int f([int] x):
    switch x:
        case []:
            return 0
        case [1]:
            return -1
    return 10

void ::main(System sys,[string] args):
    sys.out.println(toString(f([])))
    sys.out.println(toString(f([1])))
    sys.out.println(toString(f([3])))
    sys.out.println(toString(f([1,2,3])))
