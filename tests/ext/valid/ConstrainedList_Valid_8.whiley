import println from whiley.lang.System

define nat as int where $ >= 0

[int] f([nat] xs) requires |xs| > 0, ensures some { x in $ | x >= 0 }:
    return xs

void ::main(System.Console sys):
    rs = f([1,2,3])
    sys.out.println(Any.toString(rs))

