

type listibr is int[] | bool[] | real[]

function len(listibr l) -> int:
    return |l|

public export method test() :
    bool[] s = [true,false,true]
    assume len(s) == 3
    int[] l = [1, 2]
    assume len(l) == 2
    real[] m = [1.0,2.0,3.0,4.0]
    assume len(m) == 4
