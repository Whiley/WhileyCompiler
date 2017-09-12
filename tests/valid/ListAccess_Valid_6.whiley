

function f(int[] str) -> int[]|null:
    int[]|null r = null
    int i = 0 
    while i < |str|
        where !(r is null) ==> (|r| == |str|):
        if r is int[]:
            r[i] = r[0]
        else:
            r = [0; |str|]
        i = i + 1
    return r

public export method test() :
    assume f("Hello") == [0,0,0,0,0]
