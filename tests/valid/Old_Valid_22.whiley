public export method test():
    &int p = new 1
    //
    for i in 0..1
    where *p > old(*p):
        *p = *p + 1
    //
    assert *p > 1
