
function g(real x) -> (real y)
requires x <= 0.5
ensures y < 0.16:
    //
    return x / 3.0
