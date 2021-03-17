type Node is &{ List next, int data }
type List is null | Node

method next(List n):
    skip

method visit(List s):
    //
    next(s)
    next(s)

public export method main():
    List l1 = new Node{ next: null, data: 0}
    List l2 = new Node{ next: null, data: 1}
    //
    visit(l1)
    // Don't know much about l1 here
    assert l1->next != l2
    // Should know l2 is unchanged
    assert l2->data == 1
    
