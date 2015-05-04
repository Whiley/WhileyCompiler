

type nat is (int x) where x >= 0

function f({int f} v) -> (int r)
ensures r >= 0:
    //
    if v is {nat f}:
        return v.f
    //
    return 0

public export method test():
    assume f({f:1}) == 1
    assume f({f:-1}) == 0
