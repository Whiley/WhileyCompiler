import println from whiley.lang.System

define nat as int where $ >= 0

nat f(int x):
    switch x:
        case 1:
            return x-1
        case -1:
            return x + 1
    return 1

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(0)))
    sys.out.println(Any.toString(f(-1)))
    sys.out.println(Any.toString(f(-2)))










    
    
    