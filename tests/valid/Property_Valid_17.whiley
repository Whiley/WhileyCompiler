// This benchmark was created specifically to test recursive
// properties.

// A recursive property capturing the concept of
// the sum of an array
property sum(int[] arr, int i, int j, int s) -> (bool r):
    (i >= j && s == 0) ||
    (i < j && sum(arr,i+1,j,s-arr[i]))

function sum(int[] xs) -> (int r)
// This really produces the sum
ensures sum(xs,0,|xs|,r):
    //
    int i = 0
    int x = 0
    //
    while i < |xs| where i >= 0 && i <= |xs| && sum(xs,0,i,x):
        x = x + xs[i]
        i = i + 1
    //
    return x

public export method test():
    //
    assert sum([]) == 0
    //
    assert sum([0]) == 0
    //
    assert sum([0,1]) == 1
    //
    assert sum([1,2,3]) == 6