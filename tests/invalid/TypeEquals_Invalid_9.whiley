type List is null | { int data, List next }

function f(bool|int|null x) -> (bool r):
    //
    if !(x is null || x is int):
        return false
    else:
        return x