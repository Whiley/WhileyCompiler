import println from whiley.lang.System

define nat as int where $ >= 0

int f(int x) requires x >= 0, ensures $ == 0 || $ == 1:
    switch x:
        case 1:
            return 1
        default:
            return 0

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(0)))










    
    
    