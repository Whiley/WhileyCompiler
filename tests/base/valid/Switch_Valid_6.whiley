import * from whiley.lang.*

int f(int x):    
    switch x:
        case 1:
            y = -1
            break
        case 2:
            y = -2
            break
        default:
            y = 0
    return y

void ::main(System sys,[string] args):
    sys.out.println(str(f(1)))
    sys.out.println(str(f(2)))
    sys.out.println(str(f(3)))
    sys.out.println(str(f(-1)))
