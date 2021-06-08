public export method test():
    &int q = new 1
    &int p = new 2
    //
    for i in 0..10:
        int x = *p
        *q = 1
    //
    assert *p == 2
