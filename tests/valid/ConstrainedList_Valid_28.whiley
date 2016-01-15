import whiley.lang.System

// A recursive implementation of the lastIndexOf algorithm
function lastIndexOf(int[] items, int item, int index) -> (int r)
// Index is within bounds or one past length
requires index >= 0 && index <= |items|
// If positive result, it is at position index or above
ensures r >= 0 ==> r >= index
// If positive result, element at its position matches item
ensures r >= 0 ==> items[r] == item
// If positive result, no item above result matches item 
ensures r >= 0 ==> all { i in r+1 .. |items| | items[i] != item }
// If negative result, no item above index matches item
ensures r < 0 ==> all { i in index .. |items| | items[i] != item }:
    // ...
    if index == |items|:
        return -1
    else:
        int rest = lastIndexOf(items,item,index+1)
        if rest < 0 && items[index] == item:
            return index
        else:
            return rest

public export method test():
    //
    int[] arr = [1,2,3,2,3,4,1,2,3]
    //
    assume lastIndexOf(arr,3,0) == 8
    //
    assume lastIndexOf(arr,4,0) == 5
    //
    assume lastIndexOf(arr,-1,0) == -1


        
