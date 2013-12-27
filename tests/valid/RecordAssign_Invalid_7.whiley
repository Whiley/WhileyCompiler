import println from whiley.lang.System

define bytes as {int b1, int b2}

bytes f(int b):
    return {b1:b,b2:2}

void ::main(System.Console sys):
    b = 1
    bs = f(b)
    sys.out.println(Any.toString(bs))
    bs = {b1:b,b2:b}
    sys.out.println(Any.toString(bs))
