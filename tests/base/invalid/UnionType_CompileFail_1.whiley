import * from whiley.lang.*

// this is a comment!
define IntReal as int | real

int f(int x):
    return x

void ::main(System.Console sys):
    if |args| > 0:
        x = 1.23
    else:
        x = 1
    sys.out.println(Any.toString(x))
    f(x)

