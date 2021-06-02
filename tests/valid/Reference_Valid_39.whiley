type List is &{int data, null|List next }

method fill(List l, int v)
ensures l->data == v:
    l->data = v

public export method test():
    // Allocate two links
    List l1 = new {data:0, next:null}
    List l2 = new {data:0, next:l1}
    // Invoke method
    fill(l1,1)
    // Check postcondition
    assert l1->data == 1
    // Invoke method
    fill(l2,2)
    // Check postcondition
    assert l2->data == 2
    


