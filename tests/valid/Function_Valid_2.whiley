import println from whiley.lang.System

type fr3nat is int where $ >= 0

function f(int x) => string:
    return Any.toString(x)

method main(System.Console sys) => void:
    y = 1
    sys.out.println(f(y))
