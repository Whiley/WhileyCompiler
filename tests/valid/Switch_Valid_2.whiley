import println from whiley.lang.System

int f(int x):    
    switch x:
        case 1:
            y = -1
        case 2:
            y = -2
        default:
            y = 0
    return y

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(3)))
    sys.out.println(Any.toString(f(-1)))
