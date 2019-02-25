function select<S,T>(bool f, S s, T t) -> (S|T r)
ensures (r == s) || (r == t):
    if f:
        return s
    else:
        return t

public export method test():
    int x = select(true,1,2)
    int y = select(false,1,2)    
    //
    assert x == 1
    assert y == 2
    