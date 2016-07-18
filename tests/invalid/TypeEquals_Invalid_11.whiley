type List is null | { int data, List next }

function f(int|null|bool x) -> (bool r):
    //
    if x is null || x is int:
        return false
    else:
        return x == 0