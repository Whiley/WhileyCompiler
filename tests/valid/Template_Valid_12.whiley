function id<T>(T x) -> (T y)
ensures x == y:
    return x

public export method test():
    int x = id(1)
    bool b = id(true)
    //
    assert x == 1
    assert b == true
    