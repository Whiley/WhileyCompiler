type Cyclic is { null|&Cyclic ptr, int data }

public export method test():
    &Cyclic p = new {ptr:null, data: 0}
    &Cyclic q = new {ptr:p, data: 0}
    // Make it cyclic!
    p->ptr = q
    //
    assert p != q
    assert p->data == q->data
