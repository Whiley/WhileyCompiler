type fun_t is function(int)->(int)

unsafe function f(int x) -> (int y):
    assert x >= 0
    return x

unsafe public export method test():
    fun_t x = &f(int)
    //
    assert f(1) == 1
