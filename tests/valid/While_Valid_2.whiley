import whiley.lang.*

// The classic binary search which runs in O(log n) time by halving
// the search space on each iteration until either the item is found, or
// the search space is emtpy.  Its fair to say that this is quite a test
// for the verifier!!
function binarySearch([int] items, int item) -> (bool result)
// The input list must be in sorted order
requires all { i in 0 .. |items|-1 | items[i] < items[i+1] }
// If return true, then matching item must exist in items
ensures result ==> some { i in items | i == item }
// If return false, then no matching item exists in items
ensures !result ==> no { i in items | i == item }:
    //
    int lo = 0
    int hi = |items|

    while lo < hi
        where 0 <= lo && hi <= |items| && lo <= hi
        where no { i in 0 .. lo | items[i] == item }
        where no { i in hi .. |items| | items[i] == item }:
        //
        // Note, the following is safe in Whiley because we have
        // unbounded integers.  If that wasn't the case, then this could
        // potentially overflow leading to a very subtle bug (like that
        // eventually found in the Java Standard Library).
        //
        int mid = (lo + hi) / 2

        if items[mid] < item:
            lo = mid + 1
        else if items[mid] > item:
            hi = mid
        else:
            return true
    //
    return false

method main(System.Console console):
    [int] list = [3,5,6,9]
    console.out.println(list)
    for i in 0 .. 10:
        console.out.println(binarySearch(list,i))
