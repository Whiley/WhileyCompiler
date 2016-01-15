

function g(real x) -> (real y)
requires x <= 0.5
ensures y <= 0.166666666666668:
    //
    return x / 3.0

public export method test() :
    assume g(0.234) == (0.234/3.0)
