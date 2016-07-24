function f(int|null x) -> (int r)
ensures x is null || r == x:
    //
    if x is int:
        return x
    else:
        return 0

public export method test():
    assume f(-1) == -1
    assume f(0)  == 0
    assume f(1)  == 1
    assume f(2)  == 2
    assume f(null) == 0

