

function f(real x) -> (real y)
requires x >= 0.2345
ensures y < -0.2:
    //
    return 0.0 - x

public export method test() :
    assume f(1.234) == -1.234
