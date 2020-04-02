type fun_t is function(int)->(int)

function inc(int x) -> (int y):
    return x+1

public export method test():
    //
    fun_t fp = &inc
    assert fp(1,1) == 1
    