

type cr1nat is int

function f(cr1nat x) -> int:
    int y = x
    return y

public export method test() :
    assume f(9) == 9
