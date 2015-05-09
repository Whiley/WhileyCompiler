type nat is (int x) where x >= 0

function f(int v) -> (int r)
ensures r >= 0:
    //
    if v is !nat:
        return 0
    //
    return v+1

public export method test():
    assume f(1) == 2
    assume f(9) == 10
    assume f(-1) == 0
    assume f(-3) == 0
