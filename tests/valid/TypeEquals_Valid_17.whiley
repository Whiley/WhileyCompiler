

type rlist is bool | int[]

function f(rlist l) -> int:
    if l is bool:
        return 0
    else:
        return |l|

public export method test() :
    assume f(true) == 0
    assume f(false) == 0
    assume f([1, 2, 3]) == 3
