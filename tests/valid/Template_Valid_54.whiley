function id<T>(T x) -> (T y)
ensures x == y:
    return x

function sel<T>(T x, T y) -> (T z)
ensures (x == y) ==> (x == z)
ensures (x != y) ==> (y == z):
    if x == y:
        return x
    else:
        return y

public export method test():
    assert id(1) == sel(1,1)
    assert id(2) == sel(1,2)