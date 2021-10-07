type Node is &{ List next, int data }
type List is null | Node

property unchanged(List l)
where l->data == old(l->data)
where l->next == old(l->next)
where l == null || unchanged(l->next)

method next(Node n) -> (List l):
    return n->next

method size(List s, List e) -> int
ensures unchanged(s)
ensures unchanged(e):
    //
    int count = 0
    //
    while (s is Node) && (s != e):
        s = next(s)
        count = count + 1
    //
    return count

public export method test():
    List l0 = null
    List l1 = new Node{next:l0,data:1}
    List l2 = new Node{next:l1,data:2}
    //
    int s0 = size(l0,l0)
    assume s0 == 0
    int s1 = size(l1,l0)    
    assume s1 == 1
    assert l1->data == 1
    int s2 = size(l2,l0)    
    assume s2 == 2
    assert l2->data == 2
    int s3 = size(l2,l1)
    assume s3 == 1
    assert l2->data == 2
    assert l1->data == 1
