

function lastIndexOf(int[] xs, int x) -> (int|null r)
ensures r is int ==> xs[r] == x:
    //
    int i = 0
    int|null last = null
    //
    while i < |xs| 
        where i >= 0
        where (last is int ==> 0 <= last && last < |xs| && xs[last] == x):
        //
        if xs[i] == x:
            last = i
        i = i + 1
    //
    return last

public export method test():
    int[] list = [1,2,1,3,1,2]
    assume lastIndexOf(list,0) == null
    assume lastIndexOf(list,1) == 4
    assume lastIndexOf(list,2) == 5
    assume lastIndexOf(list,3) == 3
