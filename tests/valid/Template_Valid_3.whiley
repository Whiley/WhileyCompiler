type LinkedList<T> is null | { LinkedList<T> next, T data }

public export method test():
    LinkedList<int> l1 = null
    LinkedList<int> l2 = { next: l1, data: 0 }
    LinkedList<int> l3 = { next: l2, data: 1 }
    //
    assert l1 == null
    assert l2.next == null
    assert l2.data == 0
    assert l3.next == l2
    assert l3.next.next == null
    assert l3.data == 1
