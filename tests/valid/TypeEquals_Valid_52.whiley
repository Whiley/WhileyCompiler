function f(int|null x) -> (bool y):
    //
    bool r = x is int && x >= 0
    //
    if x is null:
        return false
    else:
        return r

public export method test():
    assume f(-1) == false
    assume f(0) == true
    assume f(1) == true
    assume f(null) == false