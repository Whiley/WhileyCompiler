method m(&&int p)
ensures **p == 0:
    **p = 0

public export method test():
    &int q = new 123
    &(&int) r = new q
    //
    assert **r == 123
    assert **r == *q
    assert *r == q
    //
    m(r)
    //
    assert *q == 0