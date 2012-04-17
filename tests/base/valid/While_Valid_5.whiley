import println from whiley.lang.System

[int] extract([int] ls):
    i = 0
    r = [1]
    // now do the reverse!
    while i < |ls|:
        r = r + [1]
        i = i + 1
    return r

void ::main(System.Console sys):
    rs = extract([1,2,3,4,5,6,7])
    sys.out.println(Any.toString(rs))
    rs = extract([])
    sys.out.println(Any.toString(rs))
