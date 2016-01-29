

type intlist is int | int[]

function f(intlist[] l) -> any:
    return l

public export method test() :
    (int|int[])[] x

    if 0 == 0:
        x = [1, 2, 3]
    else:
        x = [[1], [2, 3], [5]]
    x[0] = 1
    //
    assume f(x) == [1,2,3]
