type nat is (int x) where x >= 0

function f(int|null x) -> null:
    //
    if x is nat:
        // (int|null)&nat ==> (int|null)&int ==> int
        return null
    else:
        // (int|null)&!nat ==> (int|null)&any ==> int|null
        return x
