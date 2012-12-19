import println from whiley.lang.System

define nat as int where $ >= 0

[nat] extract([int] ls):
    i = 0
    rs = []
    // now do the reverse!
    while i < |ls| where i >= 0 && no { r in rs | r < 0 }:
        if(ls[i] >= 0):
            rs = rs + [ls[i]]
        i = i + 1
    return rs

void ::main(System.Console sys):
    rs = extract([-2,-3,1,2,-23,3,2345,4,5])
    sys.out.println(Any.toString(rs))
