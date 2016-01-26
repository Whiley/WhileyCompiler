
function g(int y) -> int
requires y > 0:
    return 10 / y

function f(int y) 
requires y >= 0:
    g(y)
