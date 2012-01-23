import println from whiley.lang.*

define rec as {int x, int y}

int f([int]|[bool] xs):
    if xs is [int]:
        return |xs|
    else:
        return |xs|

void ::main(System.Console sys):
    e = []
    sys.out.println(f(e))
    e = [1,2,3,4]
    sys.out.println(f(e))
    e = [true,false]
    sys.out.println(f(e))
