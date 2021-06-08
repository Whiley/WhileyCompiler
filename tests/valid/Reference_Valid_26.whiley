public export method test():
    int i = 0
    &int q = new 1
    &int p = new 2
    //
    while i < 10:
        *p = 2
        i = i + 1
    //
    assert *q == 1
