type Node is &{ List next, int data }
type List is null | Node

method next(Node r):
    skip

public export method test():
    List p = new Node{next: null, data: 0}
    List q =  new Node{next: null, data: 1}
    assert q->data == 1
    next(p)
    assert p->next != q
    assert q->data == 1
