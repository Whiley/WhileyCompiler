

// Determine whether a given list of integers
// is sorted from smallest to largest.
function isSorted(int[] items) -> (bool r)
requires |items| >= 2
ensures r ==> all { j in 1 .. |items| | items[j-1] < items[j] }:
    //
    int i = 1

    while i < |items|
    where i >= 1 && i <= |items|
    where all { j in 1 .. i | items[j-1] < items[j] }:
        //
        if items[i-1] >= items[i]:
            return false
        i = i + 1

    return true

public export method test():
    assume isSorted([1,2,3,4])
    assume !isSorted([1,2,4,3])
    assume !isSorted([4,2,3,5])
    assume isSorted([0;0])
