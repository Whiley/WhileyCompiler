

type nat is (int x) where x >= 0

function f([int] v) -> (int r)
ensures r >= 0:
    //
    if v is [nat]:
        return |v|
    //
    return 0

public export method test():
    assume f([1,2,3]) == 3
    assume f([-1]) == 0
    assume f([1,0,-1]) == 0
