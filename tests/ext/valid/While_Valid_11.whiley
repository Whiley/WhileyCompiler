import println from whiley.lang.System

define nat as int where $ >= 0

[int] create(nat count, int value) ensures |$| == count:
    r = []
    i=0
    while i < count where i <= count && i == |r|:
        r = r + [value]
        i=i+1
    return r

void ::main(System.Console sys):
    sys.out.println(Any.toString(create(3,3)))
    sys.out.println(Any.toString(create(2,2)))
    sys.out.println(Any.toString(create(2,1)))
    sys.out.println(Any.toString(create(1,1)))
    sys.out.println(Any.toString(create(0,0)))

