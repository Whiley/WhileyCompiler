type nat is (int x) where x >= 0

function abs(int x) -> nat:
    if x >= 0:
        return x
    else:
        return -x

function nop(nat item) -> (nat r)
ensures item == r:
    //
    return abs(item)

public export method test() -> void:
    nat xs = abs(-123)
    assume xs == 123
    xs = nop(1)
    assume xs == 1
