type List is null | { int data, List next }

function f(bool|int|null x) -> (bool r):
    //
    if x is bool || x is int:
        return x
    else:
        return false