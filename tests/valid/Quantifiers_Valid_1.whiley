

function f(int[] xs) -> int[]
requires no { i in 0..|xs| | xs[i] < 0 }:
    return xs

public export method test() :
    int[] ys = [1, 2, 3]
    int[] zs = ys
    assume f(zs) == [1,2,3]
    assert ys == [1,2,3]
