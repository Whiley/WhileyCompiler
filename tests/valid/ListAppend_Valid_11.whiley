import println from whiley.lang.System

int f([int] xs):
    return |xs|

void ::main(System.Console sys):
    left = [1,2,3]
    right = [5,6,7]
    r = f(left + right)
    sys.out.println(Any.toString(r))
