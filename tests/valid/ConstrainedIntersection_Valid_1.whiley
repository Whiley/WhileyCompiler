type nat is (int x) where x >= 0

function f(int v) -> (int r)
ensures r >= 0:
    //
    if v is nat && v < 10:
        return 1
    //
    return 0

public export method test():
    assume f(1) == 1
    assume f(9) == 1
    assume f(10) == 0
    assume f(-1) == 0
