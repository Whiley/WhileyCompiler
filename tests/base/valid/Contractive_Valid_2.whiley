import * from whiley.lang.*
    
define C1 as null|any
define C2 as null|any
define C3 as C1|C2

C3 ::f(C1 x):
    return x

C3 ::g(C2 x):
    return x

C1 ::h(C3 x):
    return x

C2 ::i(C3 x):
    return x

void ::main(System sys, [string] args):
    x = f(null)
    sys.out.println(Any.toString(x))
