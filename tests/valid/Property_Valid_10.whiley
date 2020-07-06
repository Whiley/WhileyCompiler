// This benchmark was created specifically to test recursive
// properties.

// A recursive property capturing the concept of
// the sum of an array
property sum(int[] arr, int i, int s)
where (i >= |arr|) ==> (s == 0)
where (i >= 0 && i < |arr|) ==> sum(arr,i+1,s-arr[i])

public export method test():
    //
    assert sum([],0,0)
    //
    assert sum([0],0,0)
    //
    assert sum([0,1],0,1)
    //
    assert sum([1,2,3],0,6)