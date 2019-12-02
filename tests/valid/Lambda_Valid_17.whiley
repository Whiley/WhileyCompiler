type fn_t is function(int)->(int,int)

public export method test():
    fn_t f = &(int x -> (x,x))
    //
    (int y, int z) = f(1)
    //
    assert y == 1
    assert z == 1