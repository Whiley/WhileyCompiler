

function f(real x) -> (real y)
requires x > 0.0
ensures y < 0.0:
    return -x

public export method test() :
    assume f(1.2) == -1.2
    assume f(0.00001) == -0.00001
    assume f(5632.0) == -5632.0
