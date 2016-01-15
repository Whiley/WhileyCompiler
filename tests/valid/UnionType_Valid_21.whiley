type nat is (int x) where x >= 0
type nlist is nat | int[]

function f(int x) -> nlist:
    if x <= 0:
        return 0
    else:
        return f(x - 1)

public export method test() :
    nlist x = f(2)
    assume x == 0
