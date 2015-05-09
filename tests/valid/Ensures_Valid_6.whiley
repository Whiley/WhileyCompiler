

type nat is (int x) where x >= 0

function abs(int x) -> (int|null r)
// if we return an int, it cannot be negative
ensures r is int ==> r >= 0:
    //
    if x >= 0:
        return x
    else:
        return null

public export method test():
    assume abs(1) == 1
    assume abs(-1) == null
