import * from whiley.lang.*

// this is a comment!
define IntList as int|[int]

int f([int] xs):
    return |xs|

void ::main(System.Console sys):
    if |args| == 0:
        x = 1
        y = x // OK
    else:
        sys.out.println(Any.toString(y))
        x = [1,2,3]
        ys = x // OK
    z = f(x) // should fail
    sys.out.println(Any.toString(z))

