type nat is (int x) where x >= 0

function count_helper(int[] items, int item) -> (int len, int[] matches)
ensures |matches| == |items|
ensures all { k in 0..len | items[matches[k]] == item }:
    //
    int[] ms = [0;|items|]
    nat i = 0
    nat n = 0
    //
    while i < |items|     
    where n <= i && i <= |items| && |ms| == |items|
    where all { j in 0..n | items[ms[j]] == item }:
        if items[i] == item:
            ms[n] = i
            n = n + 1
        i = i + 1
    //
    assert i == |ms|
    assert n <= |ms|        
    //
    return n,ms

function count(int[] items, int item) -> (int count):
    // yuk
    (item,items) = count_helper(items,item)
    return item

public export method test():
    assume count([],1) == 0
    assume count([1],1) == 1
    assume count([0,1],1) == 1
    assume count([1,0,1],1) == 2
    assume count([0,1,2,3,1,2,1],1) == 3
    