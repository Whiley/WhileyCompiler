type List is null|Node
type Node is &{ int data, List next }

property unchanged(List l)
where (l is Node) ==> (l->data == old(l->data))
where (l is Node) ==> unchanged(l->next)

method m(Node n)
ensures unchanged(n->next):
    n->data = 0
    n->next = null

public export method test():
    List l1 = null
    List l2 = new {data:1,next:l1}
    List l3 = new {data:2,next:l2}
    //
    m(l3)
    assert l2->data == 1
    assert l2->next == null
