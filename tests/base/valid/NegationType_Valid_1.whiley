import * from whiley.lang.*

!null f(any x):
    if x is null:
        return 1
    else:
        return x

void ::main(System sys, [string] args):
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f([1,2,3])))
