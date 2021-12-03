type nat is (int x) where x >= 0

property same(int[] arr, int n) -> (bool r):
    all { i in 1..n | arr[i-1] == arr[i] }

function create(int start, int end) -> (int[] result)
requires start < end
ensures same(result,|result|) && |result| == (end - start):
    //
    nat i = 1
    nat length = (end - start)
    int[] items = [0;length]
    //
    while i < length 
    where i > 0 && |items| == length
    where same(items,i):
        items[i] = items[i-1]
        i = i + 1
    // Done
    return items

public export method test():
    //
    assume create(0,1) == [0]
    //
    assume create(0,2) == [0,0]
    //
    assume create(0,3) == [0,0,0]
