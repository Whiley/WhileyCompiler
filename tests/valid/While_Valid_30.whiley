

function duplicate(int n) -> (int r)
requires n >= 0
ensures  r == 2*n:
    //
    int i = 0
    int r = 0
    while i < n where i <= n && r == 2*i:
        r = r + 2
        i = i + 1
    return r

public export method test():
    assume duplicate(0) == 0
    assume duplicate(1) == 2
    assume duplicate(2) == 4
    assume duplicate(3) == 6
    assume duplicate(4) == 8
    assume duplicate(5) == 10
    assume duplicate(6) == 12
    assume duplicate(7) == 14
    assume duplicate(8) == 16
    assume duplicate(9) == 18

