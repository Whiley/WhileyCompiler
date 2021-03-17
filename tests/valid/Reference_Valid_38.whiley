type Node is &{ List next, int data }
type List is null | Node

method next(Node r):
    skip

method main():
    List p = new Node{next: null, data: 0}
    List q =  new Node{next: p, data: 1}
    Node r =  new {next: null, data: 2}
    assert r->data == 2
    next(q)
    // Check disjointness
    assert q != r
    assert q->next != r
    List qn = q->next
    assert (qn is null) || (qn->next != r)
    // Check invariance
    assert (r->data == 2)
