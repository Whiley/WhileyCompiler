import println from whiley.lang.System

define nat as int where $ >= 0

[nat] f([int] xs) requires |xs| == 0:
    return xs // sneaky ;)

void ::main(System.Console sys):
    rs = f([])
    sys.out.println(Any.toString(rs))






