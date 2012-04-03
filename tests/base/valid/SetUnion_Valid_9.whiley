import println from whiley.lang.System

bool test({real} xs, [int] ys):
    for x in (xs+ys):
        if x == 3:
            return true
    return false

void ::main(System.Console sys):
    s = test({1.2,2.3,3.4},[1,2,3,4,5,6,7,8])
    sys.out.println(Any.toString(s))
    s = test({1.2,2.3,3.4},[])
    sys.out.println(Any.toString(s))
