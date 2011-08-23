import whiley.lang.*:*

// this is a comment!
define IntList as int|[int]

int f([int] xs):
    return |xs|

void ::main(System sys,[string] args):
    if |args| == 0:
        x = 1
        y = x // OK
    else:
        sys.out.println(str(y))
        x = [1,2,3]
        ys = x // OK
    z = f(x) // should fail
    sys.out.println(str(z))

