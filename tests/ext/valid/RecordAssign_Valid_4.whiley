import * from whiley.lang.*

define bytes as {int8 b1, int8 b2}

bytes f(int8 b):
    return {b1:b,b2:2}

void ::main(System.Console sys,[string] args):
    b = 1
    bs = f(b)
    sys.out.println(Any.toString(bs))
    bs = {b1:b,b2:b}
    sys.out.println(Any.toString(bs))
