type nat is int

function g(nat[] xs) -> nat[]:
    nat[] ys = [0; |xs|]
    int i = 0
    while i < |xs|
        where i >= 0
        where |xs| == |ys|:
        //
        if xs[i] > 1:
            ys[i] = xs[i]
        i = i + 1
    return ys

function f(int[] x) -> int[]:
    return x

public export method test() :
    int[] ys = [-12309812, 1, 2, 2987, 2349872, 234987234987, 234987234987234]
    assume f(g(ys)) == [0, 0, 2, 2987, 2349872, 234987234987, 234987234987234]

