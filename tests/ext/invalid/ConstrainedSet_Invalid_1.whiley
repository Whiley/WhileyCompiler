import * from whiley.lang.*

define pintset as {int} where |$| > 1

int f(pintset x):
    return |x|

void ::main(System.Console sys):
    p = {1}
    debug Any.toString(p)
    f(p)
