method count(&int p, int n)
requires n > 0
ensures old(*p) < *p:
    //
    for i in 0..n:
        *p = *p + 1

public export method test():
    &int q = new 1
    //
    count(q,1)
    assert *q > 1
    //
    count(q,1)
    assert *q > 2