import * from whiley.lang.*

function f(bool x, int y, int z) -> int:
    return x || (y <= (z + 1))
