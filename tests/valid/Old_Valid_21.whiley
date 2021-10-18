method f(&int p):
    int n = *p
    //
    for i in 0..1
    where *p > old(*p):
        *p = *p + 1
    //
    assert *p > n

public export method test():
    f(new 0)