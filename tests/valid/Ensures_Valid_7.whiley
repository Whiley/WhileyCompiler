

function abs(int|null x) -> (int r)
// if we return an int, it cannot be negative
ensures x is int ==> (r == x || r == -x)
// return value cannot be negative
ensures r >= 0:
    //
    if x is int:
        if x >= 0:
            return x
        else:
            return -x
    else:
        return 0


public export method test():
    assume abs(1) == 1
    assume abs(-1) == 1
    assume abs(null) == 0
