import println from whiley.lang.System

define bytes as {i8 b1, i8 b2}

bytes f(i8 b):
    return {b1:b,b2:2}

void ::main(System.Console sys):
    b = 1
    bs = f(b)
    sys.out.println(Any.toString(bs))
    bs = {b1:b,b2:b}
    sys.out.println(Any.toString(bs))
