type nat is (int x) where x >= 0

property max(int[] xs, int max, int n) -> (bool r):
    // Max is not smaller than everything upto n
    all { i in 0 .. n | max >= xs[i] } &&
    // Max is one of the values upto n
    some { i in 0..n | max == xs[i] }

// Determine the maximum value of an integer array
function max(int[] items) -> (int r)
// Input array cannot be empty
requires |items| > 0
// Return is max over all items
ensures max(items,r,|items|):
    //
    nat i = 1
    int m = items[0]
    //
    while i < |items|
    where i <= |items|
    where max(items,m,i):
        if items[i] > m:
            m = items[i]
        i = i + 1
    //
    return m

public export method test():
    int[] items = [4,3,1,5,4]
    assume max([1,2,3]) == 3
    assume max([4,3,1,5,4]) == 5
    