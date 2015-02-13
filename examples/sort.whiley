/**
 * Perform a merge sort of integer items.
 */
function sort([int] items) -> [int]:
    //
    if |items| > 1:
        // First, sort left and right sub-lists
        int pivot = |items| / 2
        [int] lhs = sort(items[..pivot])
        [int] rhs = sort(items[pivot..])
        // Second, merge left and right sublists into
        // original list.
        int l = 0 // left sublist index
        int r = 0 // right sublist index
        int i = 0 // items index
        while i < |items| && l < |lhs| && r < |rhs|:
            if lhs[l] <= rhs[r]:
                items[i] = lhs[l]
                l=l+1
            else:
                items[i] = rhs[r]
                r=r+1
            i=i+1
        // Tidy up remaining items in left sublist
        while l < |lhs|:
            items[i] = lhs[l]
            i=i+1
            l=l+1
        // Tidy up remaining items in right sublist
        while r < |rhs|:
            items[i] = rhs[r]
            i=i+1
            r=r+1
    // Done
    return items

method main(System.Console sys):
    sys.out.println(sort([]))
    sys.out.println(sort([4,3,5,2,1]))
    sys.out.println(sort([3,4,7,1,2]))
    sys.out.println(sort([3,4,7,2]))
    sys.out.println(sort([2,3,4,2]))
    sys.out.println(sort([1,2,3,4]))
    sys.out.println(sort([1,2,3,4,5]))
