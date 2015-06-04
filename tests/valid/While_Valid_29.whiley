

function find([int] items, int item) -> (int r)
// Return value is within bounds of items or one past
ensures 0 <= r && r <= |items|
// If return within bounds then value at index must be item
ensures r != |items| ==> items[r] == item:
    //
    int i = 0
    while i < |items| where 0 <= i && i <= |items|:
        if items[i] == item:
            assert 0 <= i && i < |items|
            break
        i = i + 1
    // done
    return i

public export method test():
    assume find([1,2,3],1) == 0
    assume find([1,2,3],2) == 1
    assume find([1,2,3],3) == 2
    assume find([1,2,3],-1) == 3
