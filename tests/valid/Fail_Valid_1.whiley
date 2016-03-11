type nat is (int x) where x >= 0
type neg is (int x) where x < 0

function f(int|null x) -> bool|null:
    //
    if x is nat:
        return true
    else if x is neg:
        return false
    else:
        fail

public export method test() :
    assume f(-1) == false
    assume f(0) == true
    assume f(1) == true