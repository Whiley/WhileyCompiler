function count(int n) -> (int r)
ensures r == n || r == 0:
    //
    int i = 0
    //
    do:
        if n <= 0:
            break
        i = i + 1
    while i < n where n > 0 && i >= 0 && i <= n
    //
    return i

public export method test():
    assume count(-1) == 0
    assume count(0) == 0
    assume count(1) == 1
    assume count(2) == 2        
