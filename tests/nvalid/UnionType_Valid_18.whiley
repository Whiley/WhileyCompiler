import println from whiley.lang.System

type utr12nat is int where $ >= 0

type intList is utr12nat | [int]

type tupper is {int op, intList il} where (op >= 0) && (op <= 5)

function f(tupper y) => int
ensures $ >= 0:
    return y.op

method main(System.Console sys) => void:
    x = {op: 1, il: 1}
    sys.out.println(Any.toString(x))
    f(x)
