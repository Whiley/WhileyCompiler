function f(int x) -> (bool r)
requires f(x)
ensures f(x):
    //
    return false

public export method test():
    //
    assert f(0)

