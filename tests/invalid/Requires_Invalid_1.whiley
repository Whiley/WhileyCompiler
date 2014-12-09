
function g(int y) -> int
requires y > 0:
    return 10 / y

function f(int y) -> void
requires y >= 0:
    g(y)
