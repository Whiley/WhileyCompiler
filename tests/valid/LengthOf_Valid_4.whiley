

type listibr is int[] | bool[]

function len(listibr l) -> int:
    if l is int[]:
        return |l|
    else:
        return |l|

public export method test() :
    bool[] s = [true,false,true]
    assume len(s) == 3
    int[] l = [1, 2]
    assume len(l) == 2
