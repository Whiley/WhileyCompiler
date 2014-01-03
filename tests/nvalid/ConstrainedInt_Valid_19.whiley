import println from whiley.lang.System

type a_nat is int where $ >= 0

type b_nat is int where (2 * $) >= $

function f(a_nat x) => b_nat:
    if x == 0:
        return 1
    else:
        return f(x - 1)

method main(System.Console sys) => void:
    x = |sys.args|
    x = f(x)
    sys.out.println(Any.toString(x))
