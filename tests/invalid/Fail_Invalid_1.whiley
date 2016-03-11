type nat1 is (int x) where x >= 1
type neg is (int x) where x < 0

function f(int|null x) -> bool|null:
    //
    if x is nat1:
        return true
    else if x is neg:
        return false
    else:
        fail