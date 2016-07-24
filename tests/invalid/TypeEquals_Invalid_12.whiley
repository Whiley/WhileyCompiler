function f(int|null x, int|null y) -> (int r)
ensures r == 0 || r == x:
    //
    x = y
    //
    if x is int && x >= 0:
        return x
    else:
        return 0
