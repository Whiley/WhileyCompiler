type nat is (int x) where x >= 0
type nlist is int | nat[]

function f(int i, nlist[] xs) -> nlist:
    if (i < 0) || (i >= |xs|):
        return 0
    else:
        return xs[i]

public export method test() :
    nlist x = f(2, [2, 3, 4])
    assume x == 4
