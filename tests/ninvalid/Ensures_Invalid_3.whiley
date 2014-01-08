
function g(int y) => int
requires y >= 0
ensures $ > 0:
    return y

function f(int y) => int
requires y > 0
ensures $ >= 0:
    return g(y)
