

type expr is int[] | bool

function cons(int item, int[] input) -> int[]:
    int[] result = [0; |input|+1]
    int i = 0
    //
    while i < |input|
        where i >= 0
        where |result| == |input|+1:
        //
        result[i+1] = input[i]
        i = i + 1
    //
    result[0] = item
    return result

function f(expr e) -> int[]:
    if e is int[]:
        int[] t = cons(0,e)
        return t
    else:
        return [0;0]

public export method test() :
    int[] e = [1, 2, 3, 4]
    assume f(e) == [0, 1, 2, 3, 4]
    assume f(false) == [0;0]
