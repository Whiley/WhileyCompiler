function f(int|null x) -> (int r):
    //
    if !(x is int && x >= 0):
        return 0
    else:
        return x

public export method test():
    assume f(-1) == 0
    assume f(0) == 0
    assume f(1) == 1
    assume f(2) == 2    
    assume f(null) == 0