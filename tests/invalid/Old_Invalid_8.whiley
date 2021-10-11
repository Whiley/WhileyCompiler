method m(&int p)
ensures *p != old(*p):
    //
    *p = *p + 1

method g():
    &int q = new 1
    m(q)
    // Cannot conclude following
    assert *q == 1