import println from whiley.lang.System

void ::main(System.Console sys):
    left = [1,2]
    right = [3,4]
    r = left + right
    left = left + [6]
    sys.out.println(Any.toString(left))
    sys.out.println(Any.toString(right))
    sys.out.println(Any.toString(r))
