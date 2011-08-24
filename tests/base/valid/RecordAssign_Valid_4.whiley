import whiley.lang.*:*

define bytes as {int b1, int b2}

bytes f(int b):
    return {b1:b,b2:2}

void ::main(System sys,[string] args):
    b = 1
    bs = f(b)
    sys.out.println(str(bs))
    bs = {b1:b,b2:b}
    sys.out.println(str(bs))
