property nat(int x) -> (bool r):
    return x >= 0

function abs(int x) -> (int y)
ensures nat(y)
ensures (x == y) || (x == -y):
    //
    if x >= 0:
        return x
    else:
        return -x

public export method test():
    assert abs(-1) == 1
    assert abs(-2) == 2
    assert abs(0) == 0
    assert abs(1) == 1
    assert abs(2) == 2
    