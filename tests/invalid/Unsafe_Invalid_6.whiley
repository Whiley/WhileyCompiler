type meth_t is method(int)->(int)

unsafe method f(int x) -> (int y):
    assert x >= 0
    return x

unsafe public export method test():
    meth_t m = &f
    //
    int x = m(1)
    //
    assert x == 1
