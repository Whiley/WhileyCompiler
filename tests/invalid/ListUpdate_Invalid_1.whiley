import println from whiley.lang.System

[int] f([int] xs):
    // should fail?
    xs[0] = 1
    return xs

void ::main(System.Console sys):
    rs = f([])
    sys.out.println(Any.toString(rs))






