

method f(int[] args) -> int
requires |args| >= 2:
    //
    int r = 0
    int i = 0
    do:
        r = r + args[i]
        i = i + 1
    while (i + 1) < |args| where i >= 0
    //
    return r

public export method test() :
    assume f([1, 2, 3]) == 3
    assume f([1, 2]) == 1
    assume f([1, 2, 3, 4, 5, 6]) == 15
