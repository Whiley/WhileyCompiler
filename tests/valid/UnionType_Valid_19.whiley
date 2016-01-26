

type utr12nat is int

type intList is utr12nat | int[]

type tupper is {int op, intList il}

function f(tupper y) -> int:
    return y.op

public export method test() :
    tupper x = {op: 1, il: 1}
    assume f(x) == 1
