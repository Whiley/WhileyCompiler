import println from whiley.lang.System

// this is a comment!
define num as {1,2,3,4}
define bignum as {1,2,3,4,5,6,7}

string f(num x):
    y = x
    return Any.toString(y)

string g({bignum} zs, int z):
    if z in zs && z in num:
        return f(z)
    else:
        return "MISS"

void ::main(System.Console sys):
    sys.out.println(g({1,2,3,5},3))
