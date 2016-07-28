type List is null | { int data, List next }

function f(int|null x) -> (bool r):
    //
    if !(x is null || x >= 0):
        return x
    else:
        return false