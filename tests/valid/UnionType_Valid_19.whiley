import whiley.lang.*

type utr12nat is int

type intList is utr12nat | [int]

type tupper is {int op, intList il}

function f(tupper y) -> int:
    return y.op

method main(System.Console sys) -> void:
    tupper x = {op: 1, il: 1}
    sys.out.println(x)
    f(x)
