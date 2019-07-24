type Cyclic is { null|&Cyclic ptr, int data }

public export method test():
    &Cyclic p = new (Cyclic) {ptr:null, data: 0}
    &Cyclic q = new (Cyclic) {ptr:p, data: 0}
    // Make it cyclic!
    p->ptr = q
    //
    assume p != q
    assume *p == *q
