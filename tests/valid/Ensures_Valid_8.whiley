

function abs(int x) -> (int r)
ensures r >= 0:
    //
    if x < 0:
        x = -x
    //
    return x

public export method test():
    assume abs(1) == 1
    assume abs(-1) == 1
