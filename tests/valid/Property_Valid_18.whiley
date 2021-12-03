property sum(int[] arr, int i, int s) -> (bool r):
    (i <= 0 && s == 0) ||
    (i > 0 && sum(arr, i-1, s - arr[i-1]))

property sum(int[] arr, int s) -> (bool r):
    sum(arr,|arr|,s)

function sum(int[] xs) -> (int r):
    //
    int i = 0
    int x = 0
    //
    while i < |xs| where i >= 0 && i <= |xs| && sum(xs,i,x):
        x = x + xs[i]
        i = i + 1
    //
    return x

public export method test():
    assert sum([],0)
    assert sum([1],1)
    assert sum([1,2],3)
    assert sum([1,2,3],6)
    assert sum([1,2,3,4],10)
