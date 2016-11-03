type pos is (int x) where x > 0

function f(pos|null x) -> null:
    //
    if x is int && x >= 0:
        return null
    else if x is null:
        return x
    else:
        fail

public export method test():
    assume f(1) == null
    assume f(2) == null
    assume f(null) == null
    