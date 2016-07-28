function count(int n) -> (int r)
requires n >= 0
ensures r == n:
    //
    int i = 0
    //
    while i < n where i >= 0 && i <= n:
        if i == 3:
            i = i + 2
            continue
        i = i + 1
    //
    return i

public export method test():
    assume count(0) == 0
    assume count(1) == 1
    assume count(2) == 2        
