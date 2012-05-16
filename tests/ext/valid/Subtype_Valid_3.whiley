import println from whiley.lang.System

define sr3nat as int where $ > 0

void ::main(System.Console sys):
    x = [1]
    x[0] = 1
    sys.out.println(Any.toString(x))
    
