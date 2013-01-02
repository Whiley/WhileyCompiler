import println from whiley.lang.System

define nat as int where $ >= 0

[nat] g([nat] xs):
    return xs

[nat] f([nat] xs):
    return g(xs)

void ::main(System.Console sys):
    rs = f([1,2,3])
    sys.out.println(Any.toString(rs))

