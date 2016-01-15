type rec is {int y, int x}
type nbool is bool|null

function f(int[] xs) -> nbool[]:
    nbool[] r = [false; |xs|]
    int i = 0
    while i < |xs|
        where i >= 0
        where |xs| == |r|:
        //
        if xs[i] < 0:
            r[i] = true
        else:
            r[i] = null
        i = i + 1
    return r

public export method test() :
    int[] e = [0;0]
    assume f(e) == [false;0]
    e = [1, 2, 3, 4]
    assume f(e) == [null,null,null,null]
