function id<T>(T x) -> (T y)
ensures x == y:
    return x

public export method test():
    int x = id(1)
    //
    assert x == 1