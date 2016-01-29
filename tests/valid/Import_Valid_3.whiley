


function toInt(int[] ls) -> int:
    int r = 0
    int i = 0
    while i < |ls| where i >= 0:
        r = r + ls[i]
        i = i + 1
    return r

public export method test() :
    int[] ls = [1, 2, 3, 4]
    assume toInt(ls) == 10
