
function g(int y) -> (int x)
requires y >= 0
ensures x > 0:
    //
    return y

function f(int y) -> (int x)
requires y > 0
ensures x >= 0:
    //
    return g(y)
