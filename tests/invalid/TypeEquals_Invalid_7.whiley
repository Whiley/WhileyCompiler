function f(int|null x) -> (bool|null r):
    //
    if x is int && x >= 0:
        return false
    else:
        return x
