

type nat is (int n) where n >= 0

type T is int | int[]

function f(T x) -> int:
    if x is int[] | nat:
        return 0
    else:
        return x

public export method test() :
    assume f(1) == 0
    assume f(-1) == -1
    assume f([1, 2, 3]) == 0
