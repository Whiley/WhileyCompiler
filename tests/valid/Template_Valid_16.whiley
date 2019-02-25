type LinkedList<T> is null | { LinkedList<T> next, T data }

property length<T>(LinkedList<T> list, int n)
where length(list.next,n-1)

public export method test():
    LinkedList<int> l1 = null
    LinkedList<int> l2 = { next: l1, data: 1 }
    LinkedList<int> l3 = { next: l2, data: 2 }
    //
    assert length(l1,0)
    assert length(l2,1)
    assert length(l3,2)
    