

function f(int[] x) -> int[]
requires x[0] == 0:
    assert x[0] == 0
    return x

function g(int[] x) -> int[]
requires x[0] == 0:
    assert |x| > 0
    return x

public export method test() :
    assume f([0, 1, 2]) == [0,1,2]
    assume g([0]) == [0]
    assume g([0, 1, 2]) == [0,1,2]
