function count(int n) -> (int r)
requires n > 0
ensures r == n:
    //
    int i = 1
    //
    do:
        i = i - 1
    while i < n where i >= 0 && i <= n
    //
    return i

public export method test():
    assume count(1) == 1
    assume count(2) == 2        
