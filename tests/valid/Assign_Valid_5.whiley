public export method test():
    int[] x = [0]
    int y = 0
    //
    x[0], y = 1,x[0]
    //
    assert x == [1]
    assert y == 0
    
