type nat is (int x) where x >= 0

property sorted(int[] arr, int n)
where all { i in 1..n | arr[i-1] <= arr[i] }

function bubbleSort(int[] items) -> (int[] result)
// Resulting array is sorted
ensures sorted(result,|result|):
    //
    int tmp
    bool clean
    //
    do:
        // reset clean flag
        clean = true
        nat i = 0
        // look for unsorted pairs
        while i < |items|
        // If clean, everything so far sorted
        where clean ==> sorted(items,i):
            if items[i-1] > items[i]:
               // found unsorted pair
               clean = false
               tmp = items[i-1]
               items[i-1] = items[i]
               items[i] = tmp
            i = i + 1
    while !clean
    // If clean, whole array is sorted
    where clean ==> sorted(items,|items|)
    // Done
    return items

public export method test():
    //
    assume bubbleSort([0]) == [0]
    //
    assume bubbleSort([1,0]) == [0,1]
    //
    assume bubbleSort([1,0,4,3]) == [0,1,3,4]

