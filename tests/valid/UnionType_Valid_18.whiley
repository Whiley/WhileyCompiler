

type utr12nat is (int x) where x >= 0

type intList is utr12nat | int[]

type tupper is ({int op, intList il} this) where (this.op >= 0) && (this.op <= 5)

function f(tupper y) -> (int result)
ensures result >= 0:
    //
    return y.op

public export method test() :
    tupper x = {op: 1, il: 1}
    assume f(x) == 1
