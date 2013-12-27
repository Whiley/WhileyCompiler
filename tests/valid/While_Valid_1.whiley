import println from whiley.lang.System

[int] reverse([int] ls):
    i = |ls|
    r = []
    // now do the reverse!
    while i > 0:
        i = i - 1
        r = r + [ls[i]]
    return r

void ::main(System.Console sys):
    rs = reverse([1,2,3,4,5])
    sys.out.println(Any.toString(rs))
