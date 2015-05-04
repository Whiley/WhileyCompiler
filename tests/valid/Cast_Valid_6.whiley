

function divide(real lhs, int rhs) -> real
requires rhs > 0:
    //
    real tmp = (real) rhs
    //
    return lhs / tmp

public export method test():
    assume divide(10.0,2) == 5.0
    assume divide(10.0,3) == (5.0 / 1.5)
