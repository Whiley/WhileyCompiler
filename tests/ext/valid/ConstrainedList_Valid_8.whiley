import println from whiley.lang.System

define nat as int where $ >= 0
define somenat as [int] where some { x in $ | x >= 0 }

int f(somenat xs):
    return 1

int g([nat] xs) requires |xs| > 0:
    return f(xs)

void ::main(System.Console sys):
    rs = g([1,2,3])
    sys.out.println(Any.toString(rs))

