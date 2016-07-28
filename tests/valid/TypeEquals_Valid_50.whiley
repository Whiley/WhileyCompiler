function f(bool|int|null x) -> (int r):
    //
    if x is null || x is bool:
        return 0
    else:
        return x

public export method test():
    assume f(0) == 0
    assume f(1) == 1
    assume f(2) == 2
    assume f(false) == 0
    assume f(true) == 0    
    assume f(null) == 0