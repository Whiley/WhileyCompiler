function min2(int x, int y) -> (int r)
ensures x <= y && r == x || x >= y && r == y:
    if x <= y:
        r = x
    else:
        r = y
    return r
