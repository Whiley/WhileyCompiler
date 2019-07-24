public export method test():
    &int p = new 1
    &int q = new 1
    //
    assume *p == *q
    assume p != q
    //
    *p = 2
    //
    assume *p != *q
