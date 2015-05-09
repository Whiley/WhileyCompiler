

type nat is (int x) where x >= 0

function f(nat x, int y) -> nat
requires y > 0:
    int z
    if true:
        z = x / y
    else:
        z = y / x
    return z

public export method test() -> void:
    assume f(10, 2) == 5

