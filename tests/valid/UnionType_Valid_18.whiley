import whiley.lang.*

type utr12nat is (int x) where x >= 0

type intList is utr12nat | [int]

type tupper is {int op, intList il} where (op >= 0) && (op <= 5)

function f(tupper y) -> (int result)
ensures result >= 0:
    //
    return y.op

method main(System.Console sys) -> void:
    tupper x = {op: 1, il: 1}
    sys.out.println(x)
    f(x)
