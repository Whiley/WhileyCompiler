// Reverse a linked list
type LinkedList<T> is null | &{ T data, LinkedList<T> next }

property equiv<T>(LinkedList<T> l, int i, T[] items) -> (bool r):
    if i >= |items|:
        return l == null
    else if !(l is null) && l->data == items[i]:
        return equiv(l->next,i+1,items)
    else:
        return false

public export method test():
    LinkedList<int> l1 = null
    LinkedList<int> l2 = new { data: 2, next: l1 }
    LinkedList<int> l3 = new { data: 3, next: l2 }
    //
    assert equiv(l1,0,[])    
    assert equiv(l2,0,[2])
    assert equiv(l3,0,[3,2])
    
    
