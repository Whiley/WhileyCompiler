import println from whiley.lang.System

define wmcr6tup as {int x, int y}

int ::get():
    return 1

wmcr6tup ::f(int y):
    return {x:y,y:get()}

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(1)))
