import println from whiley.lang.System

define nat as int where $ >= 0

[nat] f([[nat]] xs) requires |xs| > 0:
    return xs[0]

void ::main(System.Console sys):
    rs = f([[1,2,3],[4,5,6]])
    sys.out.println(Any.toString(rs))






