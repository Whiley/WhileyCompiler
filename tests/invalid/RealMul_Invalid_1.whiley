
function f(real x, real y) -> (real y)
requires (x >= 0.5) && (y >= 0.3)
ensures y > 0.65:
    //
    return 0.5 + (x * y)
