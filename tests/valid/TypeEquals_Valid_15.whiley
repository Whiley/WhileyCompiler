

type src is int | int[] | int[][]

function f(src e) -> bool:
    if e is any[]:
        return true
    else:
        return false

public export method test() :
    assume f([1, 2, 3]) == true
    assume f([[1], [2]]) == true
    assume f(1) == false
