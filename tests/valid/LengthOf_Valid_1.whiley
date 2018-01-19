

type list is int[] | bool[]

function len(list l) -> int:
    if l is int[]:
        return |l|
    else:
        return |l|

public export method test() :
    int[] l = [1, 2, 3]
    assume len(l) == 3
    bool[] s = [true,false,true]
    assume len(s) == 3
