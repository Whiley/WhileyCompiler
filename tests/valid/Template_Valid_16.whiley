type LinkedList<T> is null | { LinkedList<T> next, T data }

property length<T>(LinkedList<T> list, int n)
where !(list is null) ==> length<T>(list.next,n-1)

public export method test():
    LinkedList<int> l1 = null
    LinkedList<int> l2 = { next: l1, data: 1 }
    LinkedList<int> l3 = { next: l2, data: 2 }
    //
    assert length<int>(l1,0)
    assert length<int>(l2,1)
    assert length<int>(l3,2)
    