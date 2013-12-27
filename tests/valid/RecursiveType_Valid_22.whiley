import println from whiley.lang.System

// A sorted list has all data elements in ascending order.
define SortedList as null | SortedListNode
define SortedListNode as { 
    int data, SortedList next
} where next == null || data < next.data

// Create a SortedList from a data item, and a tail (which may be
// null).
SortedList SortedList(int head, SortedList tail) 
    requires tail == null || head < tail.data:
    //
    return { data: head, next: tail }

// Check whether the given item appears in the list or not.
bool contains(int item, SortedList list):
    if list == null:
        return false
    else if list.data == item:
        // We've found the item
        return true
    else if list.data > item:
        // We've gone past the point where the item would be;
        // therefore, it's not in the list.
        return false
    else:
        // Keep looking for the item
        return contains(item,list.next)

void ::main(System.Console sys):
    list = SortedList(10,null)
    list = SortedList(5,list)
    list = SortedList(3,list)
    list = SortedList(1,list)
    list = SortedList(0,list)
    sys.out.println(contains(2,list))
    sys.out.println(contains(3,list))
    sys.out.println(contains(10,list))

