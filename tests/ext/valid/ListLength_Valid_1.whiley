import println from whiley.lang.System

define nat as int where $ >= 0

nat f([int] xs):
    return |xs|

void ::main(System.Console sys):
    rs = f([1,2,3])
    sys.out.println(Any.toString(rs))
    rs = f([])
    sys.out.println(Any.toString(rs))

