
type nat is (int x) where x >= 0
type pos is (int x) where x > 0

function f(pos x, int y) -> nat
requires y > 0:
    int z
    if true:
        z = x / y
    else:
        z = y / x
    return z

public export method test() :
    assume f(10, 2) == 5

