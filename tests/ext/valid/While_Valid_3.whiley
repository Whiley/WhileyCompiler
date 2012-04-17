import println from whiley.lang.System

define nat as int where $ >= 0

[nat] extract([int] ls):
    i = 0
    r = []
    // now do the reverse!
    while i < |ls|:
        if(ls[i] >= 0):
            r = r + [ls[i]]
        i = i + 1
    return r

void ::main(System.Console sys):
    rs = extract([-2,-3,1,2,-23,3,2345,4,5])
    sys.out.println(Any.toString(rs))
