

function indexOf([int] items, int item) -> (int r)
ensures r == |items| || items[r] == item:
    //
    int i = 0
    //
    while i < |items| where i >= 0 && i <= |items|:
        if items[i] == item:
            break    
        i = i + 1
    //
    return i

public export method test():
    assume indexOf([1,2,3,4],0) == 4
    assume indexOf([1,2,3,4],1) == 0
    assume indexOf([1,2,3,4],2) == 1
    assume indexOf([1,2,3,4],3) == 2
    assume indexOf([1,2,3,4],4) == 3
